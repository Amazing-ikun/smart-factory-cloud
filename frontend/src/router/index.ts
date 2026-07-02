import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { ElMessage } from 'element-plus'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/Home',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页', requiresAuth: true },
    children: [
      {
        path: 'Info',
        name: 'InfoPage',
        component: () => import('@/views/InfoPage.vue'),
        meta: { title: '系统首页', requiresAuth: true }
      },
      {
        path: 'profile',
        name: 'ProfilePage',
        component: () => import('@/views/ProfilePage.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      },
      {
        path: 'product',
        name: 'Product',
        component: () => import('@/views/ProductPage.vue'),
        meta: { title: '产品管理', requiresAuth: true }
      },
      {
        path: 'equipment',
        name: 'Equipment',
        component: () => import('@/views/EquipmentPage.vue'),
        meta: { title: '设备管理', requiresAuth: true }
      },
      {
        path: 'order',
        name: 'Order',
        component: () => import('@/views/OrderPage.vue'),
        meta: { title: '订单管理', requiresAuth: true }
      },
      {
        path: 'equipment-product',
        name: 'EquipmentProduct',
        component: () => import('@/views/EquipmentProductPage.vue'),
        meta: { title: '设备-产品关联', requiresAuth: true }
      },
      {
        path: 'plan',
        name: 'Plan',
        component: () => import('@/views/PlanPage.vue'),
        meta: { title: '生产计划', requiresAuth: true }
      },
      {
        path: 'schedule',
        name: 'Schedule',
        component: () => import('@/views/SchedulePage.vue'),
        meta: { title: '生产调度', requiresAuth: true }
      },
      {
        path: 'daily-work',
        name: 'DailyWork',
        component: () => import('@/views/DailyWorkPage.vue'),
        meta: { title: '日报工', requiresAuth: true }
      },
      {
        path: 'logs',
        name: 'Logs',
        component: () => import('@/views/LogPage.vue'),
        meta: { title: '日志查询', requiresAuth: true }
      },
      {
        path: 'inbox',
        name: 'Inbox',
        component: () => import('@/views/InboxPage.vue'),
        meta: { title: '消息中心', requiresAuth: true }
      },
      {
        path: 'users',
        name: 'UserManage',
        component: () => import('@/views/UserManagement.vue'),
        meta: { title: '用户管理', requiresAuth: true }
      }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    document.title = to.meta.title as string
  }
  const requiresAuth = to.meta.requiresAuth !== false
  const userId = sessionStorage.getItem('userId')
  const isLoggedIn = !!userId

  if (requiresAuth && !isLoggedIn) {
    ElMessage.warning('请先登录')
    next('/')
  } else if (!requiresAuth && isLoggedIn && to.path === '/') {
    next('/Home/Info')
  } else {
    next()
  }
})

export default router
