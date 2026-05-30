<script lang="ts" setup>
import router from '@/router'
import type {ResultVO} from '@/type/ResultVO'
import {computed, ref} from 'vue'
import {RouterView, useRoute} from 'vue-router'

const route = useRoute()
const checking = ref(true)

const activeMenu = computed(() => route.path)

fetch(import.meta.env.VITE_API_BASE_URL + 'user/init', {
    method: 'GET',
    credentials: 'include',
    headers: {
        'Content-Type': 'application/json'
    }
})
    .then((response) => response.json() as Promise<ResultVO<boolean>>)
    .then((res) => {
        if (res && res.code === '00000' && res.data) {
            ElMessage.info('首次启动，请先创建管理员账号')
            router.push({path: '/init'})
            return
        }
        checking.value = false
    })
    .catch((error) => {
        checking.value = false
        console.error(error)
        ElMessage.error('无法连接后端服务')
    })

const handleSelect = (index: string) => {
    router.push(index)
}
</script>

<template>
  <el-container class="admin-shell" v-loading="checking">
    <el-aside class="admin-aside" width="220px">
      <div class="brand">
        <span class="brand-title">MoLiAPI</span>
        <span class="brand-subtitle">后台管理</span>
      </div>
      <el-menu :default-active="activeMenu" class="admin-menu" @select="handleSelect">
        <el-menu-item index="/system">系统配置</el-menu-item>
        <el-menu-item index="/daily">每日签到</el-menu-item>
        <el-menu-item index="/navigation">导航管理</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="admin-header">
        <div>
          <h1>{{ route.meta.title ?? '后台管理' }}</h1>
          <span>配置、签到和导航数据集中管理</span>
        </div>
      </el-header>
      <el-main class="admin-main">
        <RouterView/>
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.admin-shell {
    min-height: 100vh;
    background: #f5f7fb;
}

.admin-aside {
    background: #ffffff;
    border-right: 1px solid #e5e7eb;
}

.brand {
    display: flex;
    flex-direction: column;
    justify-content: center;
    height: 72px;
    padding: 0 20px;
    border-bottom: 1px solid #e5e7eb;
}

.brand-title {
    color: #1f2937;
    font-size: 20px;
    font-weight: 700;
}

.brand-subtitle {
    color: #6b7280;
    font-size: 13px;
}

.admin-menu {
    border-right: 0;
}

.admin-header {
    display: flex;
    align-items: center;
    height: 72px;
    padding: 0 28px;
    background: #ffffff;
    border-bottom: 1px solid #e5e7eb;
}

.admin-header h1 {
    margin: 0;
    color: #111827;
    font-size: 20px;
    font-weight: 700;
}

.admin-header span {
    color: #6b7280;
    font-size: 13px;
}

.admin-main {
    padding: 24px;
}
</style>
