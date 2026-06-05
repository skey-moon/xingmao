<template>
  <div class="home-container">
    <div class="welcome-section">
      <h1>欢迎使用兴茂食材配送管理系统</h1>
      <p>今日数据概览 · 实时更新</p>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <div class="stat-card stat-card-1" @click="$router.push('/food/list')">
          <div class="stat-icon">
            <el-icon :size="32"><Food /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-num">{{ stats.foodCount || 0 }}</p>
            <p class="stat-label">食材种类</p>
          </div>
          <div class="stat-trend trend-up">
            <el-icon><Top /></el-icon>
            <span>活跃</span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-card-2" @click="$router.push('/order/list')">
          <div class="stat-icon">
            <el-icon :size="32"><Document /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-num">{{ stats.todayOrderCount || 0 }}</p>
            <p class="stat-label">待处理订单</p>
          </div>
          <div class="stat-trend trend-warning">
            <el-icon><Warning /></el-icon>
            <span>待处理</span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-card-3" @click="$router.push('/delivery/list')">
          <div class="stat-icon">
            <el-icon :size="32"><Van /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-num">{{ stats.deliveryCount || 0 }}</p>
            <p class="stat-label">配送中</p>
          </div>
          <div class="stat-trend trend-active">
            <el-icon><Loading /></el-icon>
            <span>进行中</span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-card-4" @click="$router.push('/user/list')">
          <div class="stat-icon">
            <el-icon :size="32"><User /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-num">{{ stats.userCount || 0 }}</p>
            <p class="stat-label">用户数量</p>
          </div>
          <div class="stat-trend trend-up">
            <el-icon><Top /></el-icon>
            <span>正常</span>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 快捷操作 + 提示 -->
    <el-row :gutter="20" style="margin-top: 24px">
      <el-col :span="16">
        <el-card class="quick-card">
          <template #header>
            <div class="card-header">
              <span>快捷操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <div class="quick-btn primary" @click="$router.push('/food/list')">
              <el-icon :size="28"><Food /></el-icon>
              <span>食材管理</span>
            </div>
            <div class="quick-btn success" @click="$router.push('/order/list')">
              <el-icon :size="28"><Document /></el-icon>
              <span>订单管理</span>
            </div>
            <div class="quick-btn warning" @click="$router.push('/delivery/list')">
              <el-icon :size="28"><Van /></el-icon>
              <span>配送管理</span>
            </div>
            <div class="quick-btn danger" @click="$router.push('/user/list')">
              <el-icon :size="28"><User /></el-icon>
              <span>用户管理</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="info-card">
          <template #header>
            <div class="card-header">
              <span>系统信息</span>
            </div>
          </template>
          <div class="info-list">
            <div class="info-item">
              <span class="info-label">系统版本</span>
              <span class="info-value">v1.0.0</span>
            </div>
            <div class="info-item">
              <span class="info-label">当前时间</span>
              <span class="info-value">{{ currentTime }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">数据库状态</span>
              <span class="info-value status-online">在线</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, onMounted, onUnmounted, ref } from 'vue'
import { getDashboardStats } from '@/api/stat'

const stats = reactive({
  foodCount: 0,
  todayOrderCount: 0,
  deliveryCount: 0,
  userCount: 0
})

const currentTime = ref('')
let timeTimer = null

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

const loadStats = async () => {
  try {
    const result = await getDashboardStats()
    Object.assign(stats, result.data)
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

onMounted(() => {
  loadStats()
  updateTime()
  timeTimer = setInterval(updateTime, 1000)
})

onUnmounted(() => {
  if (timeTimer) clearInterval(timeTimer)
})
</script>

<style scoped>
.home-container {
  padding: 0;
}

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

.stats-row {
  margin-bottom: 10px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  position: relative;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid transparent;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-right: 16px;
}

.stat-card-1 .stat-icon { background: linear-gradient(135deg, #409EFF, #66b1ff); }
.stat-card-2 .stat-icon { background: linear-gradient(135deg, #67C23A, #85ce61); }
.stat-card-3 .stat-icon { background: linear-gradient(135deg, #E6A23C, #ebb563); }
.stat-card-4 .stat-icon { background: linear-gradient(135deg, #F56C6C, #f78989); }

.stat-info {
  flex: 1;
}

.stat-num {
  font-size: 28px;
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

.stat-trend {
  position: absolute;
  top: 16px;
  right: 16px;
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
}

.trend-up { background: #f0f9eb; color: #67C23A; }
.trend-warning { background: #fdf6ec; color: #E6A23C; }
.trend-active { background: #ecf5ff; color: #409EFF; }

.quick-card, .info-card {
  border-radius: 12px;
  border: none;
}

.card-header {
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

.info-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.info-label {
  color: #909399;
  font-size: 14px;
}

.info-value {
  color: #303133;
  font-size: 14px;
  font-weight: 500;
}

.status-online {
  color: #67C23A;
}
</style>