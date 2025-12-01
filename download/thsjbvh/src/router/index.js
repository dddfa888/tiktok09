import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/shop-cart',
    name: 'ShopCart',
    component: () => import('../views/shopcart/index.vue')
  },
  {
    path: '/shop-cart-2',
    name: 'ShopCart2',
    component: () => import('../views/shopcart/index_backup.vue')
  },
  {
    path: '/pos-logs',
    name: 'PosLogs',
    component: () => import('../views/poslogs/index.vue')
  },
  {
    path: '/activity',
    name: 'Activity',
    meta: { title: '活动管理' },
    redirect: '/activity/list',
    component: () => import('../views/activity/view.vue'),
    children: [
      {
        path: 'list',
        name: 'ActivityList',
        meta: { title: '活动列表' },
        component: () => import('@/views/activity/list/index.vue')
      },
      {
        path: 'prize',
        name: 'ActivityPrize',
        meta: { title: '奖品管理' },
        component: () => import('@/views/activity/prize/index.vue')
      },
      {
        path: 'lottery-record',
        name: 'ActivityLotteryRecord',
        meta: { title: '中奖记录' },
        component: () => import('@/views/activity/lotteryRecord/index.vue')
      },
      {
        path: 'lottery-receive',
        name: 'ActivityLotteryReceive',
        meta: { title: '领奖记录' },
        component: () => import('@/views/activity/lotteryReceive/index.vue')
      },
    ]
  },
  {
    path: '/mall-goods',
    name: 'MallGoods',
    meta: { title: '商品库' },
    component: () => import('@/views/mallGoods/index.vue')
  },
  {
    path: '/demo',
    name: 'demo',
    meta: { title: 'demo' },
    redirect: '/demo/g-add',
    component: () => import('@/views/demo/index.vue'),
    children: [
      {
        path: 'g-add',
        name: 'demoGadd',
        component: () => import('@/views/demo/g-add.vue')
      },
      {
        path: 'g-edit',
        name: 'demoGedit',
        component: () => import('@/views/demo/g-edit.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router