import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Dashboard',
    component: () => import('@/views/DashboardView.vue')
  },
  {
    path: '/analysis',
    name: 'Analysis',
    component: () => import('@/views/AnalysisView.vue')
  },
  {
    path: '/sample',
    name: 'Sample',
    component: () => import('@/views/SampleView.vue')
  },
  {
    path: '/box',
    name: 'Box',
    component: () => import('@/views/BoxView.vue')
  },
  {
    path: '/boxpos',
    name: 'BoxPos',
    component: () => import('@/views/BoxPosView.vue')
  },
  {
    path: '/log',
    name: 'Log',
    component: () => import('@/views/LogView.vue')
  },
  {
    path: '/threshold',
    name: 'Threshold',
    component: () => import('@/views/ThresholdView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
