import {createRouter, createWebHistory} from 'vue-router'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'main',
            component: () => import('@/views/MainView.vue')
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
        },
        {
            path: '/daily',
            name: 'daily',
            component: () => import('@/views/DailyView.vue')
        }
    ]
})

export default router
