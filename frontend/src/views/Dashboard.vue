<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h1>兴茂食材配送 - 数据大屏</h1>
      <div class="header-right">
        <span class="current-time">{{ currentTime }}</span>
        <el-tag type="success" v-if="isAdmin">管理员视图</el-tag>
        <el-button type="danger" size="small" @click="handleLogout">
          <el-icon><SwitchButton /></el-icon> 退出
        </el-button>
      </div>
    </div>

    <!-- 管理员专属：财务流水 -->
    <div class="finance-section" v-if="isAdmin">
      <div class="section-title">财务流水概览</div>
      <div class="finance-cards">
        <div class="finance-card income">
          <div class="finance-label">总收入</div>
          <div class="finance-value">¥{{ finance.totalIncome }}</div>
        </div>
        <div class="finance-card expense">
          <div class="finance-label">总支出</div>
          <div class="finance-value">¥{{ finance.totalExpense }}</div>
        </div>
        <div class="finance-card profit">
          <div class="finance-label">净利润</div>
          <div class="finance-value">¥{{ finance.netProfit }}</div>
        </div>
        <div class="finance-card today">
          <div class="finance-label">今日收入</div>
          <div class="finance-value">¥{{ finance.todayIncome }}</div>
        </div>
        <div class="finance-card today-amount">
          <div class="finance-label">今日交易</div>
          <div class="finance-value">¥{{ finance.todayAmount || 0 }}</div>
        </div>
      </div>
    </div>

    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #409EFF, #66b1ff)">
          <el-icon size="32"><Food /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.foodCount }}</div>
          <div class="stat-label">食材种类</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #67C23A, #85ce61)">
          <el-icon size="32"><Document /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.orderCount }}</div>
          <div class="stat-label">总订单数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #E6A23C, #ebb563)">
          <el-icon size="32"><Van /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.deliveryCount }}</div>
          <div class="stat-label">配送中</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #F56C6C, #f78989)">
          <el-icon size="32"><User /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.userCount }}</div>
          <div class="stat-label">用户总数</div>
        </div>
      </div>
    </div>

    <div class="charts-row">
      <div class="chart-card">
        <div class="chart-title">订单状态分布</div>
        <div class="chart-content">
          <div class="status-distribution">
            <div class="status-item">
              <span class="status-dot" style="background: #909399"></span>
              <span class="status-label">待处理</span>
              <span class="status-value">{{ stats.pendingOrders }}</span>
            </div>
            <div class="status-item">
              <span class="status-dot" style="background: #E6A23C"></span>
              <span class="status-label">配送中</span>
              <span class="status-value">{{ stats.deliveringOrders }}</span>
            </div>
            <div class="status-item">
              <span class="status-dot" style="background: #67C23A"></span>
              <span class="status-label">已完成</span>
              <span class="status-value">{{ stats.completedOrders }}</span>
            </div>
            <div class="status-item">
              <span class="status-dot" style="background: #F56C6C"></span>
              <span class="status-label">已取消</span>
              <span class="status-value">{{ stats.cancelledOrders }}</span>
            </div>
          </div>
        </div>
      </div>
      <div class="chart-card">
        <div class="chart-title">配送员状态</div>
        <div class="chart-content">
          <div class="delivery-status">
            <div class="delivery-item">
              <el-icon size="24" color="#67C23A"><SuccessFilled /></el-icon>
              <span class="delivery-label">空闲配送员</span>
              <span class="delivery-count">{{ deliveryStats.freeCount }}</span>
            </div>
            <div class="delivery-item">
              <el-icon size="24" color="#E6A23C"><Van /></el-icon>
              <span class="delivery-label">配送中</span>
              <span class="delivery-count">{{ deliveryStats.busyCount }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="orders-table">
      <div class="table-title">实时订单动态</div>
      <el-table :data="recentOrders" stripe class="orders-data-table">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="totalPrice" label="金额" width="100">
          <template #default="{ row }">
            <span class="price">¥{{ row.totalPrice }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="110">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" class="status-tag status-pending">待处理</el-tag>
            <el-tag v-if="row.status === 1" class="status-tag status-delivering">配送中</el-tag>
            <el-tag v-if="row.status === 2" class="status-tag status-completed">已完成</el-tag>
            <el-tag v-if="row.status === 3" class="status-tag status-cancelled">已取消</el-tag>
            <el-tag v-if="row.status === 4" class="status-tag status-pending-confirm">已送达待确认</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="address" label="配送地址" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="下单时间" width="170" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getStatData, getTransactionSummary } from '@/api/stat'
import { getOrderList } from '@/api/order'

const router = useRouter()
const currentTime = ref('')
let timer = null

const isAdmin = computed(() => localStorage.getItem('role') === 'admin')

const stats = ref({
  foodCount: 0,
  orderCount: 0,
  deliveryCount: 0,
  userCount: 0,
  pendingOrders: 0,
  deliveringOrders: 0,
  completedOrders: 0,
  cancelledOrders: 0
})

const finance = ref({
  totalIncome: 0,
  totalExpense: 0,
  netProfit: 0,
  todayIncome: 0,
  todayAmount: 0
})

const deliveryStats = ref({
  freeCount: 0,
  busyCount: 0
})

const recentOrders = ref([])

const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const handleLogout = () => {
  localStorage.clear()
  ElMessage.success('已退出登录')
  router.push('/home')
}

const loadStats = async () => {
  try {
    const result = await getStatData({})
    if (result.data) {
      stats.value = { ...stats.value, ...result.data }
    }
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

const loadFinance = async () => {
  if (!isAdmin.value) return
  try {
    const result = await getTransactionSummary()
    if (result.data) {
      finance.value = result.data
    }
  } catch (error) {
    console.error('加载财务数据失败', error)
  }
}

const loadRecentOrders = async () => {
  try {
    const result = await getOrderList({ pageNum: 1, pageSize: 20 })
    if (result.data && result.data.records) {
      recentOrders.value = result.data.records

      const orders = result.data.records
      stats.value.orderCount = result.data.total || orders.length
      stats.value.pendingOrders = orders.filter(o => o.status === 0).length
      stats.value.deliveringOrders = orders.filter(o => o.status === 1).length
      stats.value.completedOrders = orders.filter(o => o.status === 2).length
      stats.value.cancelledOrders = orders.filter(o => o.status === 3).length
      stats.value.deliveryCount = stats.value.deliveringOrders
    }
  } catch (error) {
    console.error('加载订单数据失败', error)
  }
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
  loadStats()
  loadFinance()
  loadRecentOrders()
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.dashboard-container {
  background: linear-gradient(135deg, #0a0a1e 0%, #1a1a3e 100%);
  min-height: 100vh;
  padding: 20px;
  color: #fff;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 0 20px;
}

.dashboard-header h1 {
  font-size: 28px;
  font-weight: bold;
  background: linear-gradient(90deg, #409EFF, #67C23A);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.current-time {
  font-size: 20px;
  color: #409EFF;
  font-weight: 500;
}

.finance-section {
  margin-bottom: 30px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 15px;
  color: #409EFF;
}

.finance-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.finance-card {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.finance-card.income { border-left: 4px solid #67C23A; }
.finance-card.expense { border-left: 4px solid #F56C6C; }
.finance-card.profit { border-left: 4px solid #409EFF; }
.finance-card.today { border-left: 4px solid #E6A23C; }
.finance-card.today-amount { border-left: 4px solid #909399; }

.finance-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 8px;
}

.finance-value {
  font-size: 28px;
  font-weight: bold;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-info { flex: 1; }

.stat-value {
  font-size: 36px;
  font-weight: bold;
  color: #fff;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  margin-top: 4px;
}

.charts-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 30px;
}

.chart-card {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 20px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.chart-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #409EFF;
}

.chart-content {
  height: 150px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.status-distribution, .delivery-status {
  display: flex;
  flex-direction: column;
  gap: 16px;
  width: 100%;
  padding: 10px 20px;
}

.status-item, .delivery-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.status-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.status-label, .delivery-label {
  flex: 1;
  font-size: 14px;
}

.status-value, .delivery-count {
  font-size: 24px;
  font-weight: bold;
}

.orders-table {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 20px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.table-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #409EFF;
}

.orders-data-table {
  background: transparent;
  color: #fff;
}

.orders-data-table :deep(.el-table__header-wrapper th) {
  background: rgba(64, 158, 255, 0.3);
  color: #fff;
}

.orders-data-table :deep(.el-table__body-wrapper tr) {
  background: rgba(255, 255, 255, 0.05);
}

.orders-data-table :deep(.el-table__body-wrapper td) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.orders-data-table :deep(.el-table__body-wrapper tr:hover td) {
  background: rgba(64, 158, 255, 0.2);
}

.price {
  color: #F56C6C;
  font-weight: 600;
}

/* 订单状态标签 - 高对比度样式 */
.status-tag {
  font-weight: 600;
}

.status-pending {
  background: rgba(144, 147, 153, 0.8) !important;
  border-color: #909399 !important;
  color: #fff !important;
}

.status-delivering {
  background: rgba(230, 162, 60, 0.8) !important;
  border-color: #E6A23C !important;
  color: #fff !important;
}

.status-completed {
  background: rgba(103, 194, 58, 0.8) !important;
  border-color: #67C23A !important;
  color: #fff !important;
}

.status-cancelled {
  background: rgba(245, 108, 108, 0.8) !important;
  border-color: #F56C6C !important;
  color: #fff !important;
}

.status-pending-confirm {
  background: rgba(255, 165, 0, 0.8) !important;
  border-color: #FFA500 !important;
  color: #fff !important;
}
</style>