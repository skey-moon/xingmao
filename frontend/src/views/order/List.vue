<template>
  <div class="page-container">
    <div class="page-header">
      <h2>订单管理</h2>
      <el-button @click="loadData">
        <el-icon><Refresh /></el-icon> 刷新
      </el-button>
    </div>

    <el-card class="table-card">
      <!-- 搜索工具栏 -->
      <div class="toolbar">
        <el-input v-model="searchOrderNo" placeholder="搜索订单号" style="width: 180px" clearable @clear="loadData"
          @keyup.enter="loadData" prefix-icon="Search" />
        <el-select v-model="filterStatus" placeholder="订单状态" style="width: 150px" clearable @change="loadData">
          <el-option label="待处理" :value="0" />
          <el-option label="配送中" :value="1" />
          <el-option label="已送达待确认" :value="4" />
          <el-option label="已完成" :value="2" />
          <el-option label="已取消" :value="3" />
        </el-select>
        <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期"
          end-placeholder="结束日期" value-format="YYYY-MM-DD" style="margin-right: 10px" @change="loadData" />
        <el-button type="primary" @click="loadData">
          <el-icon><Search /></el-icon> 搜索
        </el-button>
        <el-button @click="clearFilters">清除筛选</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" stripe class="data-table" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="orderNo" label="订单号" width="180">
          <template #default="{ row }">
            <span class="order-no">{{ row.orderNo }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="totalPrice" label="总金额" width="120" sortable>
          <template #default="{ row }">
            <span class="price">¥{{ row.totalPrice }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120" sortable>
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="info" effect="plain">待处理</el-tag>
            <el-tag v-if="row.status === 1" type="warning" effect="plain">配送中</el-tag>
            <el-tag v-if="row.status === 2" type="success" effect="plain">已完成</el-tag>
            <el-tag v-if="row.status === 3" type="danger" effect="plain">已取消</el-tag>
            <el-tag v-if="row.status === 4" type="warning" effect="plain" style="background: #E6A23C; border-color: #E6A23C; color: #fff;">已送达待确认</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="address" label="配送地址" min-width="150" show-overflow-tooltip />
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip>
          <template #default="{ row }">{{ row.remark || '-' }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="170" sortable />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="handleDetail(row)">
              <el-icon><View /></el-icon> 详情
            </el-button>
            <el-button v-if="row.status === 0" type="success" size="small" text @click="handleDispatch(row)">
              <el-icon><Van /></el-icon> 派单
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
            <el-tag v-if="currentOrder.status === 4" type="warning" style="background: #E6A23C; border-color: #E6A23C; color: #fff;">已送达待确认</el-tag>
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

    <!-- 派单弹窗 -->
    <el-dialog v-model="dispatchVisible" title="分配配送员" width="500px" :close-on-click-modal="false">
      <el-form :model="dispatchForm" label-width="100px">
        <el-form-item label="订单号">
          <el-input v-model="dispatchForm.orderNo" disabled />
        </el-form-item>
        <el-form-item label="配送地址">
          <el-input v-model="dispatchForm.address" disabled />
        </el-form-item>
        <el-form-item label="选择配送员">
          <el-select v-model="dispatchForm.deliveryPersonId" placeholder="请选择配送员" style="width: 100%">
            <el-option v-for="person in deliveryPersons" :key="person.id" :label="person.name + ' (当前' + person.orderCount + '单)'" :value="person.id">
              <span>{{ person.name }}</span>
              <span style="color: #909399; font-size: 12px; margin-left: 8px">电话: {{ person.phone }} | 进行中: {{ person.orderCount }}单</span>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dispatchVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAutoAssign">自动分配</el-button>
        <el-button type="success" @click="handleConfirmDispatch" :loading="dispatchLoading">确认派单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getOrderList, getOrderDetail, dispatchOrder, confirmReceipt } from '@/api/order'
import { getDeliveryPersons, autoAssignDelivery, assignDelivery } from '@/api/delivery'

const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filterStatus = ref('')
const searchOrderNo = ref('')
const dateRange = ref([])
const loading = ref(false)

const detailVisible = ref(false)
const currentOrder = ref(null)
const orderItems = ref([])
const currentDelivery = ref(null)

const dispatchVisible = ref(false)
const dispatchLoading = ref(false)
const dispatchForm = reactive({
  orderId: null,
  orderNo: '',
  address: '',
  deliveryPersonId: null
})
const deliveryPersons = ref([])

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
    if (searchOrderNo.value) {
      params.orderNo = searchOrderNo.value
    }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const result = await getOrderList(params)
    tableData.value = result.data.records
    total.value = result.data.total
  } catch (error) {
    console.error('加载数据失败', error)
  } finally {
    loading.value = false
  }
}

const clearFilters = () => {
  filterStatus.value = ''
  searchOrderNo.value = ''
  dateRange.value = []
  loadData()
}

const handleDetail = async (row) => {
  try {
    const res = await getOrderDetail(row.id)
    console.log('订单详情响应:', res)

    if (!res || res.code !== 200) {
      ElMessage.error(res?.msg || '获取详情失败')
      return
    }

    if (!res.data) {
      ElMessage.error('订单不存在')
      return
    }

    currentOrder.value = res.data.order
    orderItems.value = res.data.items || []
    currentDelivery.value = res.data.delivery || null
    detailVisible.value = true
  } catch (error) {
    console.error('加载订单详情失败', error)
    ElMessage.error('加载详情失败')
  }
}

const handleDispatch = async (row) => {
  dispatchForm.orderId = row.id
  dispatchForm.orderNo = row.orderNo
  dispatchForm.address = row.address
  dispatchForm.deliveryPersonId = null

  try {
    const result = await getDeliveryPersons()
    deliveryPersons.value = result.data || []
    dispatchVisible.value = true
  } catch (error) {
    console.error('加载配送员失败', error)
  }
}

const handleAutoAssign = async () => {
  if (!dispatchForm.orderId) return
  dispatchLoading.value = true
  try {
    await dispatchOrder(dispatchForm.orderId)
    ElMessage.success('自动分配成功，订单已派送')
    dispatchVisible.value = false
    loadData()
  } catch (error) {
    console.error('自动分配失败', error)
    ElMessage.error('自动分配失败')
  } finally {
    dispatchLoading.value = false
  }
}

const handleConfirmDispatch = async () => {
  if (!dispatchForm.deliveryPersonId) {
    ElMessage.warning('请选择配送员')
    return
  }
  dispatchLoading.value = true
  try {
    const person = deliveryPersons.value.find(p => p.id === dispatchForm.deliveryPersonId)
    await assignDelivery({
      orderId: dispatchForm.orderId,
      deliveryPersonId: dispatchForm.deliveryPersonId,
      deliveryName: person.name,
      deliveryPhone: person.phone
    })
    ElMessage.success('派单成功')
    dispatchVisible.value = false
    loadData()
  } catch (error) {
    console.error('派单失败', error)
    ElMessage.error('派单失败')
  } finally {
    dispatchLoading.value = false
  }
}

const handleConfirmReceipt = async () => {
  try {
    await confirmReceipt(currentOrder.value.id)
    ElMessage.success('确认收货成功')
    detailVisible.value = false
    loadData()
  } catch (error) {
    console.error('确认收货失败', error)
    ElMessage.error('确认收货失败')
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
  flex-wrap: wrap;
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
