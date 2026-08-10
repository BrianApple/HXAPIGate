import { createRouter, createWebHashHistory } from 'vue-router'
import { getUserInfo } from '../api'

const routes = [
  { path: '/login', component: () => import('../views/Login.vue') },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    redirect: '/welcome',
    children: [
      { path: 'welcome', component: () => import('../views/Welcome.vue'), meta: { title: '首页' } },
      { path: 'apiType', component: () => import('../views/ApiType.vue'), meta: { title: 'API类型管理' } },
      { path: 'api', component: () => import('../views/ApiList.vue'), meta: { title: 'API管理' } },
      { path: 'log', component: () => import('../views/LogSearch.vue'), meta: { title: '日志查询' } },
      { path: 'role', component: () => import('../views/RoleList.vue'), meta: { title: '角色管理' } },
      { path: 'roleAuth/:id', component: () => import('../views/RoleAuth.vue'), meta: { title: '角色授权' } }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 路由守卫：未登录跳转登录页
router.beforeEach((to) => {
  if (to.path !== '/login' && !getUserInfo()) {
    return '/login'
  }
  if (to.path === '/login' && getUserInfo()) {
    return '/'
  }
  return true
})

export default router
