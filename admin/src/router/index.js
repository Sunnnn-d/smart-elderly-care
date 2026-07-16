import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    component: () => import('../layout/AdminLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/dashboard/Index.vue'),
        meta: { title: '首页概览', icon: 'DataAnalysis' }
      },
      {
        path: 'elderly',
        name: 'Elderly',
        component: () => import('../views/elderly/Index.vue'),
        meta: { title: '老人档案', icon: 'User' }
      },
      {
        path: 'service',
        name: 'Service',
        component: () => import('../views/service/Index.vue'),
        meta: { title: '服务项目', icon: 'Goods' }
      },
      {
        path: 'order',
        name: 'Order',
        component: () => import('../views/order/Index.vue'),
        meta: { title: '服务订单', icon: 'List' }
      },
      {
        path: 'health',
        name: 'Health',
        component: () => import('../views/health/Index.vue'),
        meta: { title: '健康监测', icon: 'FirstAidKit' }
      },
      {
        path: 'care-plan',
        name: 'CarePlan',
        component: () => import('../views/care-plan/Index.vue'),
        meta: { title: '护理计划', icon: 'Document' }
      },
      {
        path: 'notice',
        name: 'Notice',
        component: () => import('../views/notice/Index.vue'),
        meta: { title: '公告管理', icon: 'Bell' }
      },
      {
        path: 'banner',
        name: 'Banner',
        component: () => import('../views/banner/Index.vue'),
        meta: { title: '轮播图管理', icon: 'Picture' }
      },
      {
        path: 'sys-user',
        name: 'SysUser',
        component: () => import('../views/user/SysUser.vue'),
        meta: { title: '系统用户', icon: 'User' }
      },
      {
        path: 'sys-role',
        name: 'SysRole',
        component: () => import('../views/user/SysRole.vue'),
        meta: { title: '角色管理', icon: 'Key' }
      },
      {
        path: 'app-user',
        name: 'AppUser',
        component: () => import('../views/user/AppUser.vue'),
        meta: { title: '客户端用户', icon: 'Users' }
      },
      {
        path: 'message',
        name: 'Message',
        component: () => import('../views/message/Index.vue'),
        meta: { title: '消息管理', icon: 'Message' }
      },
      {
        path: 'medication',
        name: 'Medication',
        component: () => import('../views/medication/Index.vue'),
        meta: { title: '用药管理', icon: 'Pill' }
      },
      {
        path: 'emergency',
        name: 'Emergency',
        component: () => import('../views/emergency/Index.vue'),
        meta: { title: '紧急呼叫', icon: 'Phone' }
      },
      {
        path: 'bed',
        name: 'Bed',
        component: () => import('../views/bed/Index.vue'),
        meta: { title: '床位管理', icon: 'Home' }
      },
      {
        path: 'fee',
        name: 'Fee',
        component: () => import('../views/fee/Index.vue'),
        meta: { title: '费用管理', icon: 'Wallet' }
      },
      {
        path: 'payment',
        name: 'Payment',
        component: () => import('../views/payment/Index.vue'),
        meta: { title: '支付管理', icon: 'CreditCard' }
      },
      {
        path: 'activity',
        name: 'Activity',
        component: () => import('../views/activity/Index.vue'),
        meta: { title: '活动管理', icon: 'Trophy' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory('/admin/'),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title || '后台管理'} - 暖夕伴养老管理系统`
  const token = localStorage.getItem('admin_token')
  if (to.path !== '/login' && to.path !== '/register' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
