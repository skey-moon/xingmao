import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/user/Login.vue')
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue')
  },
  {
    path: '/layout',
    component: () => import('@/components/Layout.vue'),
    children: [
      {
        path: '/home',
        name: 'Home',
        component: () => import('@/views/Home.vue')
      },
      {
        path: '/food/list',
        name: 'FoodList',
        component: () => import('@/views/food/List.vue')
      },
      {
        path: '/order/list',
        name: 'OrderList',
        component: () => import('@/views/order/List.vue')
      },
      {
        path: '/order/create',
        name: 'OrderCreate',
        component: () => import('@/views/order/Create.vue')
      },
      {
        path: '/order/my',
        name: 'MyOrders',
        component: () => import('@/views/order/MyOrders.vue')
      },
      {
        path: '/customer/home',
        name: 'CustomerHome',
        component: () => import('@/views/customer/Home.vue')
      },
      {
        path: '/customer/address',
        name: 'CustomerAddress',
        component: () => import('@/views/customer/Address.vue')
      },
      {
        path: '/customer/profile',
        name: 'CustomerProfile',
        component: () => import('@/views/customer/Profile.vue')
      },
      {
        path: '/customer/review',
        name: 'CustomerReview',
        component: () => import('@/views/customer/Review.vue')
      },
      {
        path: '/user/list',
        name: 'UserList',
        component: () => import('@/views/user/List.vue')
      },
      {
        path: '/delivery/list',
        name: 'DeliveryList',
        component: () => import('@/views/delivery/List.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')

  // 如果访问需要登录的页面但未登录
  if (to.meta.requiresAuth && !token) {
    next('/login')
    return
  }

  // 用户管理页面只有 admin 能访问
  if (to.path === '/user/list') {
    if (role === 'admin') {
      next()
      return
    }
    // employee 和 user 都不能访问用户管理
    next('/home')
    return
  }

  // 管理后台页面：首页、食材管理、订单管理、配送管理
  // admin 和 employee 都能访问
  const adminRoutes = ['/home', '/food/list', '/order/list', '/delivery/list']
  if (adminRoutes.includes(to.path)) {
    if (role === 'admin' || role === 'employee') {
      next()
      return
    }
    // user 角色 -> 客户平台
    next('/customer/home')
    return
  }

  // 客户平台页面
  if (to.path === '/order/create' || to.path === '/order/my' || to.path.startsWith('/customer/')) {
    if (token) {
      next()
      return
    }
    next('/login')
    return
  }

  // 大屏页面公开访问
  if (to.path === '/dashboard') {
    next()
    return
  }

  next()
})

export default router