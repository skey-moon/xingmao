<template>
  <div class="page-container">
    <div class="page-header">
      <h2>订单评价</h2>
    </div>

    <el-card class="table-card">
      <div class="toolbar">
        <el-select v-model="filterStatus" placeholder="评价状态" style="width: 150px" clearable @change="loadData">
          <el-option label="待评价" :value="0" />
          <el-option label="已评价" :value="1" />
        </el-select>
        <el-button type="primary" @click="loadData">
          <el-icon><Search /></el-icon> 搜索
        </el-button>
        <el-button @click="clearFilters">清除筛选</el-button>
      </div>

      <el-table :data="tableData" stripe class="data-table" v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="180">
          <template #default="{ row }">
            <span class="order-no">{{ row.orderNo }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="totalPrice" label="金额" width="100">
          <template #default="{ row }">
            <span class="price">¥{{ row.totalPrice }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 2" type="success" effect="plain">已完成</el-tag>
           <el-tag v-else type="info" effect="plain">进行中</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="170" />
        <el-table-column label="评价状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.hasReview" type="info" effect="plain">已评价</el-tag>
            <el-tag v-else-if="row.status === 2" type="warning" effect="plain">待评价</el-tag>
            <el-tag v-else type="info" effect="plain">-</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="handleReview(row)"
              v-if="row.status === 2 && !row.hasReview">
              <el-icon><Star /></el-icon> 评价
            </el-button>
            <el-button type="info" size="small" text @click="handleViewReview(row)" v-if="row.hasReview">
              <el-icon><View /></el-icon> 查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination background layout="total, prev, pager, next"
          v-model:current-page="pageNum" v-model:page-size="pageSize"
          :total="total" @current-change="loadData" />
      </div>
    </el-card>

    <!-- 评价弹窗 -->
    <el-dialog v-model="reviewDialogVisible" title="评价订单" width="500px" :close-on-click-modal="false">
      <div v-if="currentOrder" class="review-form">
        <div class="review-order-info">
          <span>订单号：</span><strong>{{ currentOrder.orderNo }}</strong>
          <span style="margin-left: 16px">金额：</span><strong class="price">¥{{ currentOrder.totalPrice }}</strong>
        </div>
        <el-form :model="reviewForm" ref="reviewFormRef" label-width="80px">
          <el-form-item label="评分">
            <el-rate v-model="reviewForm.rating" allow-half show-text :texts="['很差', '较差', '一般', '满意', '非常满意']" />
          </el-form-item>
          <el-form-item label="评价内容">
            <el-input v-model="reviewForm.content" type="textarea" :rows="4" placeholder="请输入您的评价（选填）" maxlength="500" show-word-limit />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmitReview">提交评价</el-button>
      </template>
    </el-dialog>

    <!-- 查看评价弹窗 -->
    <el-dialog v-model="viewDialogVisible" title="评价详情" width="500px">
      <div v-if="currentReview" class="review-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="订单号">{{ currentReview.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="评分">
            <el-rate v-model="currentReview.rating" disabled show-text />
          </el-descriptions-item>
          <el-descriptions-item label="评价内容">{{ currentReview.content || '无' }}</el-descriptions-item>
          <el-descriptions-item label="评价时间">{{ currentReview.createTime }}</el-descriptions-item>
          <el-descriptions-item v-if="currentReview.reply" label="商家回复">
            <span style="color: #67C23A">{{ currentReview.reply }}</span>
          </el-descriptions-item>
          <el-descriptions-item v-if="currentReview.replyTime" label="回复时间">{{ currentReview.replyTime }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyOrders, getOrderDetail } from '@/api/order'
import { getReviewByOrderId, createReview, getMyReviews } from '@/api/review'

const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filterStatus = ref('')
const loading = ref(false)

const reviewDialogVisible = ref(false)
const viewDialogVisible = ref(false)
const submitLoading = ref(false)
const reviewFormRef = ref(null)
const currentOrder = ref(null)
const currentReview = ref(null)

const reviewForm = reactive({
  rating: 5,
  content: ''
})

const loadData = async () => {
  loading.value = true
  try {
    const params = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (filterStatus.value !== '') {
      params.status = 2 // 只查已完成的
    }
    const result = await getMyOrders(params)
    const orders = result.data.records || []

    // 检查每个订单是否已评价
    const ordersWithReview = await Promise.all(orders.map(async (order) => {
      try {
        const reviewRes = await getReviewByOrderId(order.id)
        return { ...order, hasReview: !!reviewRes.data }
      } catch {
        return { ...order, hasReview: false }
      }
    }))

    if (filterStatus.value === 1) {
      tableData.value = ordersWithReview.filter(o => o.hasReview)
    } else if (filterStatus.value === 0) {
      tableData.value = ordersWithReview.filter(o => !o.hasReview)
    } else {
      tableData.value = ordersWithReview
    }
    total.value = result.data.total || 0
  } catch (error) {
    console.error('加载数据失败', error)
  } finally {
    loading.value = false
  }
}

const clearFilters = () => {
  filterStatus.value = ''
  pageNum.value = 1
  loadData()
}

const handleReview = async (row) => {
  currentOrder.value = row
  reviewForm.rating = 5
  reviewForm.content = ''
  reviewDialogVisible.value = true
}

const handleViewReview = async (row) => {
  try {
    const result = await getReviewByOrderId(row.id)
    currentReview.value = { ...result.data, orderNo: row.orderNo }
    viewDialogVisible.value = true
  } catch (error) {
    console.error('加载评价失败', error)
    ElMessage.error('加载评价失败')
  }
}

const handleSubmitReview = async () => {
  if (reviewForm.rating === 0) {
    ElMessage.warning('请选择评分')
    return
  }
  submitLoading.value = true
  try {
    await createReview({
      orderId: currentOrder.value.id,
      rating: reviewForm.rating,
      content: reviewForm.content
    })
    ElMessage.success('评价成功')
    reviewDialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('评价失败', error)
    ElMessage.error(error.response?.data?.msg || '评价失败')
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-container { padding: 0; }

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.table-card { border-radius: 12px; border: none; }

.toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
}

.order-no { font-family: monospace; color: #409EFF; font-weight: 500; }
.price { color: #F56C6C; font-weight: 600; }

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.review-form { padding: 10px 0; }

.review-order-info {
  margin-bottom: 20px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
  font-size: 14px;
}

.review-detail { padding: 10px 0; }
</style>