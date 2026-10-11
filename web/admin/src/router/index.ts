import {createRouter, createWebHistory} from 'vue-router'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'main',
            component: () => import('@/views/MainView.vue'),
            redirect: '/system',
            children: [
                {
                    path: 'system',
                    name: 'system',
                    meta: {title: '系统配置'},
                    component: () => import('@/views/SystemView.vue')
                },
                {
                    path: 'daily',
                    name: 'daily',
                    meta: {title: '每日签到'},
                    component: () => import('@/views/DailyView.vue')
                },
                {
                    path: 'navigation',
                    name: 'navigation',
                    meta: {title: '导航管理'},
                    component: () => import('@/views/NavigationView.vue')
                }
            ]
        },
        {
            path: '/login',
            name: 'login',
            component: () => import('@/views/LoginView.vue')
        },
        {
            path: '/init',
            name: 'init',
            component: () => import('@/views/InitView.vue')
        }
    ]
})

export default router
