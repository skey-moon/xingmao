<template>
  <div class="page-container">
    <div class="page-header">
      <h2>我的订单</h2>
    </div>

    <el-card class="table-card">
      <!-- 搜索工具栏 -->
      <div class="toolbar">
        <el-select v-model="filterStatus" placeholder="订单状态" style="width: 150px" clearable @change="loadData">
          <el-option label="待处理" :value="0" />
          <el-option label="配送中" :value="1" />
          <el-option label="已送达待确认" :value="4" />
          <el-option label="已完成" :value="2" />
          <el-option label="已取消" :value="3" />
        </el-select>
        <el-button type="primary" @click="loadData">
          <el-icon><Search /></el-icon> 搜索
        </el-button>
        <el-button @click="clearFilters">清除筛选</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" stripe class="data-table" v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="180">
          <template #default="{ row }">
            <span class="order-no">{{ row.orderNo }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="totalPrice" label="总金额" width="120">
          <template #default="{ row }">
            <span class="price">¥{{ row.totalPrice }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="info" effect="plain">待处理</el-tag>
            <el-tag v-if="row.status === 1" type="warning" effect="plain">配送中</el-tag>
            <el-tag v-if="row.status === 2" type="success" effect="plain">已完成</el-tag>
            <el-tag v-if="row.status === 3" type="danger" effect="plain">已取消</el-tag>
            <el-tag v-if="row.status === 4" type="warning" effect="plain" style="background: #E6A23C; border-color: #E6A23C;">已送达待确认</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="address" label="配送地址" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="下单时间" width="170" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="handleDetail(row)">
              <el-icon><View /></el-icon> 详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination background layout="total, prev, pager, next" v-model:current-page="pageNum"
          v-model:page-size="pageSize" :total="total" @current-change="loadData" />
      </div>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="700px" :close-on-click-modal="false">
      <div v-if="currentOrder" class="order-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="总金额">
            <span class="price">¥{{ currentOrder.totalPrice }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag v-if="currentOrder.status === 0" type="info">待处理</el-tag>
            <el-tag v-if="currentOrder.status === 1" type="warning">配送中</el-tag>
            <el-tag v-if="currentOrder.status === 2" type="success">已完成</el-tag>
            <el-tag v-if="currentOrder.status === 3" type="danger">已取消</el-tag>
            <el-tag v-if="currentOrder.status === 4" type="warning" style="background: #E6A23C; border-color: #E6A23C;">已送达待确认</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="配送地址">{{ currentOrder.address }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ currentOrder.remark || '-' }}</el-descriptions-item>
          <el-descriptions-item label="下单时间" :span="2">{{ currentOrder.createTime }}</el-descriptions-item>
        </el-descriptions>

        <!-- 配送信息 -->
        <el-divider v-if="currentDelivery">配送信息</el-divider>
        <el-descriptions v-if="currentDelivery" :column="2" border>
          <el-descriptions-item label="配送员">{{ currentDelivery.deliveryName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentDelivery.deliveryPhone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="配送状态">
            <el-tag v-if="currentDelivery.status === 0" type="info">待取货</el-tag>
            <el-tag v-if="currentDelivery.status === 1" type="warning">配送中</el-tag>
            <el-tag v-if="currentDelivery.status === 2" type="success">已送达</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="取货时间">{{ currentDelivery.takeTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="送达时间">{{ currentDelivery.deliveryTime || '-' }}</el-descriptions-item>
        </el-descriptions>

        <el-divider>订单商品</el-divider>

        <el-table :data="orderItems" size="small" border>
          <el-table-column prop="foodName" label="食材名称" />
          <el-table-column prop="price" label="单价">
            <template #default="{ row }">¥{{ row.price }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" />
          <el-table-column prop="subtotal" label="小计">
            <template #default="{ row }">
              <span class="price">¥{{ row.subtotal }}</span>
            </template>
          </el-table-column>
        </el-table>

        <!-- 确认收货按钮 -->
        <div v-if="currentOrder.status === 4" class="confirm-receipt">
          <el-button type="success" size="large" @click="handleConfirmReceipt">
            <el-icon><CircleCheck /></el-icon> 确认收货
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyOrders, getOrderDetail, confirmReceipt } from '@/api/order'

const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filterStatus = ref('')
const loading = ref(false)

const detailVisible = ref(false)
const currentOrder = ref(null)
const orderItems = ref([])
const currentDelivery = ref(null)

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (filterStatus.value !== '') {
      params.status = filterStatus.value
    }
    const result = await getMyOrders(params)
    tableData.value = result.data.records || []
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

const handleDetail = async (row) => {
  try {
    const result = await getOrderDetail(row.id)
    currentOrder.value = result.data.order
    orderItems.value = result.data.items || []
    currentDelivery.value = result.data.delivery || null
    detailVisible.value = true
  } catch (error) {
    console.error('加载订单详情失败', error)
  }
}

const handleConfirmReceipt = async () => {
  try {
    await ElMessageBox.confirm('确认已收到货物吗？', '确认收货', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'success'
    })
    await confirmReceipt(currentOrder.value.id)
    ElMessage.success('确认收货成功')
    detailVisible.value = false
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('确认收货失败', error)
    }
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

.order-no {
  font-family: monospace;
  color: #409EFF;
  font-weight: 500;
}

.price { color: #F56C6C; font-weight: 600; }

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.order-detail { padding: 10px 0; }

.confirm-receipt {
  margin-top: 20px;
  text-align: center;
}
</style>
