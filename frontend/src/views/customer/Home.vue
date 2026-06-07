<template>
  <div class="home-container">
    <div class="welcome-section">
      <h1>欢迎回来，{{ nickname }}</h1>
      <p>兴茂食材 · 品质配送</p>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="8">
        <div class="stat-card stat-card-1" @click="$router.push('/order/my')">
          <div class="stat-icon">
            <el-icon :size="28"><Document /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-num">{{ stats.totalOrders || 0 }}</p>
            <p class="stat-label">总订单数</p>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="stat-card stat-card-2" @click="$router.push('/order/my')">
          <div class="stat-icon">
            <el-icon :size="28"><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-num">{{ stats.pendingOrders || 0 }}</p>
            <p class="stat-label">待处理</p>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="stat-card stat-card-3" @click="$router.push('/order/my')">
          <div class="stat-icon">
            <el-icon :size="28"><Van /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-num">{{ stats.deliveryOrders || 0 }}</p>
            <p class="stat-label">配送中</p>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="stats-row" style="margin-top: 16px">
      <el-col :span="8">
        <div class="stat-card stat-card-4" @click="$router.push('/order/my')">
          <div class="stat-icon">
            <el-icon :size="28"><CircleCheck /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-num">{{ stats.completedOrders || 0 }}</p>
            <p class="stat-label">已完成</p>
          </div>
        </div>
      </el-col>
      <el-col :span="16">
        <div class="stat-card stat-card-5">
          <div class="stat-icon">
            <el-icon :size="28"><Money /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-num">¥{{ stats.totalSpent || 0 }}</p>
            <p class="stat-label">累计消费</p>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-card class="quick-card" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>快捷操作</span>
        </div>
      </template>
      <div class="quick-actions">
        <div class="quick-btn primary" @click="$router.push('/order/create')">
          <el-icon :size="28"><ShoppingCart /></el-icon>
          <span>我要下单</span>
        </div>
        <div class="quick-btn success" @click="$router.push('/order/my')">
          <el-icon :size="28"><Document /></el-icon>
          <span>我的订单</span>
        </div>
        <div class="quick-btn warning" @click="$router.push('/customer/address')">
          <el-icon :size="28"><Location /></el-icon>
          <span>收货地址</span>
        </div>
        <div class="quick-btn danger" @click="$router.push('/customer/profile')">
          <el-icon :size="28"><User /></el-icon>
          <span>个人信息</span>
        </div>
      </div>
    </el-card>

    <!-- 最近订单 -->
    <el-card class="recent-card" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>最近订单</span>
          <el-button type="primary" text @click="$router.push('/order/my')">查看全部</el-button>
        </div>
      </template>
      <el-table :data="recentOrders" stripe v-loading="loading">
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
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="info" effect="plain">待处理</el-tag>
            <el-tag v-if="row.status === 1" type="warning" effect="plain">配送中</el-tag>
            <el-tag v-if="row.status === 2" type="success" effect="plain">已完成</el-tag>
            <el-tag v-if="row.status === 3" type="danger" effect="plain">已取消</el-tag>
            <el-tag v-if="row.status === 4" type="warning" effect="plain" style="background: #E6A23C; border-color: #E6A23C;">已送达待确认</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="address" label="配送地址" min-width="120" show-overflow-tooltip />
        <el-table-column prop="createTime" label="下单时间" width="170" />
      </el-table>
     <div v-if="recentOrders.length === 0 && !loading" class="empty-tip">
        暂无订单，<el-button type="primary" text @click="$router.push('/order/create')">去下单</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getCustomerStats, getRecentOrders } from '@/api/customer'

const userStore = useUserStore()
const nickname = userStore.userInfo?.nickname || '客户'

const stats = reactive({
  totalOrders: 0,
  pendingOrders: 0,
  deliveryOrders: 0,
  completedOrders: 0,
  totalSpent: 0
})

const recentOrders = ref([])
const loading = ref(false)

const loadStats = async () => {
  try {
    const result = await getCustomerStats()
    Object.assign(stats, result.data)
    if (result.data.totalSpent) {
      stats.totalSpent = Number(result.data.totalSpent).toFixed(2)
    }
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

const loadRecentOrders = async () => {
  loading.value = true
  try {
    const result = await getRecentOrders({ pageNum: 1, pageSize: 5 })
    recentOrders.value = result.data.records || []
  } catch (error) {
    console.error('加载最近订单失败', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStats()
  loadRecentOrders()
})
</script>

<style scoped>
.home-container { padding: 0; }

.welcome-section {
  margin-bottom: 24px;
  padding: 20px 30px;
  background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
  border-radius: 12px;
  color: #fff;
}

.welcome-section h1 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
}

.welcome-section p {
  margin: 0;
  opacity: 0.9;
  font-size: 14px;
}

.stats-row { margin-bottom: 0; }

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid transparent;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-right: 16px;
}

.stat-card-1 .stat-icon { background: linear-gradient(135deg, #409EFF, #66b1ff); }
.stat-card-2 .stat-icon { background: linear-gradient(135deg, #E6A23C, #ebb563); }
.stat-card-3 .stat-icon { background: linear-gradient(135deg, #F56C6C, #f78989); }
.stat-card-4 .stat-icon { background: linear-gradient(135deg, #67C23A, #85ce61); }
.stat-card-5 .stat-icon { background: linear-gradient(135deg, #909399, #a6a9b2); flex: 1; }

.stat-info { flex: 1; }

.stat-num {
  font-size: 26px;
  font-weight: bold;
  color: #303133;
  margin: 0;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin: 4px 0 0 0;
}

.quick-card, .recent-card { border-radius: 12px; border: none; }

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
  color: #303133;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.quick-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24px 16px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  color: #fff;
  gap: 12px;
}

.quick-btn span {
  font-size: 14px;
  font-weight: 500;
}

.quick-btn:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

.quick-btn.primary { background: linear-gradient(135deg, #409EFF, #66b1ff); }
.quick-btn.success { background: linear-gradient(135deg, #67C23A, #85ce61); }
.quick-btn.warning { background: linear-gradient(135deg, #E6A23C, #ebb563); }
.quick-btn.danger { background: linear-gradient(135deg, #F56C6C, #f78989); }

.order-no { font-family: monospace; color: #409EFF; font-weight: 500; }
.price { color: #F56C6C; font-weight: 600; }

.empty-tip {
  text-align: center;
  padding: 30px;
  color: #909399;
}
</style>