<template>
  <div class="layout-container">
    <!-- 左侧菜单 -->
    <el-aside width="220px">
      <div class="logo">
        <el-icon :size="24" color="#409EFF"><Food /></el-icon>
        <span>{{ systemName }}</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#1a1a2e"
        text-color="#a0a0a0"
        active-text-color="#409EFF"
        class="sidebar-menu"
        :collapse="false"
      >
        <!-- 管理员菜单 - 所有功能 -->
        <template v-if="userRole === 'admin'">
          <el-menu-item index="/home">
            <template #title>
              <el-icon><HomeFilled /></el-icon>
              <span>首页</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/food/list">
            <template #title>
              <el-icon><Food /></el-icon>
              <span>食材管理</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/order/list">
            <template #title>
              <el-icon><Document /></el-icon>
              <span>订单管理</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/delivery/list">
            <template #title>
              <el-icon><Van /></el-icon>
              <span>配送管理</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/user/list">
            <template #title>
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/dashboard">
            <template #title>
              <el-icon><DataAnalysis /></el-icon>
              <span>数据大屏</span>
            </template>
          </el-menu-item>
        </template>

        <!-- 公司员工菜单 - 除用户管理外都有 -->
        <template v-else-if="userRole === 'employee'">
          <el-menu-item index="/home">
            <template #title>
              <el-icon><HomeFilled /></el-icon>
              <span>首页</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/food/list">
            <template #title>
              <el-icon><Food /></el-icon>
              <span>食材管理</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/order/list">
            <template #title>
              <el-icon><Document /></el-icon>
              <span>订单管理</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/delivery/list">
            <template #title>
              <el-icon><Van /></el-icon>
              <span>配送管理</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/dashboard">
            <template #title>
              <el-icon><DataAnalysis /></el-icon>
              <span>数据大屏</span>
            </template>
          </el-menu-item>
        </template>

        <!-- 客户菜单 -->
        <template v-else>
          <el-menu-item index="/order/create">
            <template #title>
              <el-icon><ShoppingCart /></el-icon>
              <span>我要下单</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/order/my">
            <template #title>
              <el-icon><Document /></el-icon>
              <span>我的订单</span>
            </template>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 顶部 -->
      <el-header>
        <div class="header-left">
          <span class="page-title">{{ pageTitle }}</span>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><Avatar /></el-icon>
              <span>{{ roleName }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main>
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()

const userRole = computed(() => userStore.role || '')

const systemName = computed(() => {
  if (userRole.value === 'admin') return '兴茂管理后台'
  if (userRole.value === 'employee') return '兴茂员工平台'
  return '兴茂食材平台'
})

const roleName = computed(() => {
  if (userRole.value === 'admin') return '管理员'
  if (userRole.value === 'employee') return '公司员工'
  return '客户'
})

const activeMenu = computed(() => route.path)

const pageTitleMap = {
  '/home': '首页',
  '/food/list': '食材管理',
  '/order/list': '订单管理',
  '/order/create': '我要下单',
  '/order/my': '我的订单',
  '/delivery/list': '配送管理',
  '/user/list': '用户管理',
  '/dashboard': '数据大屏'
}

const pageTitle = computed(() => pageTitleMap[route.path] || systemName.value)

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      window.location.href = '/login'
    }).catch(() => {})
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  display: flex;
}

.el-aside {
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%);
  overflow-x: hidden;
  transition: width 0.3s;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  padding: 0 10px;
}

.sidebar-menu {
  border: none;
  background: transparent;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 220px;
}

.el-menu-item {
  margin: 4px 8px;
  border-radius: 8px;
  height: 48px;
  transition: all 0.3s;
}

.el-menu-item:hover {
  background: rgba(64, 158, 255, 0.1) !important;
  color: #409EFF !important;
}

.el-menu-item.is-active {
  background: rgba(64, 158, 255, 0.15) !important;
  color: #409EFF !important;
}

.el-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  padding: 0 20px;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.3s;
}

.user-info:hover {
  background: #f5f7fa;
}

.el-main {
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  padding: 20px;
}

/* 路由切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>