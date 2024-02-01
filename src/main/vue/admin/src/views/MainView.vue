<script lang="ts" setup>
import router from '@/router'
import type {ResultVO} from '@/type/ResultVO'

fetch(import.meta.env.VITE_API_BASE_URL + 'init', {
  method: 'get',
  headers: {
    'Content-Type': 'application/json'
  }
})
    .then((r) => r.json() as Promise<ResultVO<Boolean>>)
    .then((res) => {
      if (res && res.code === '00000' && res.data === true) {
        ElMessage('前往初始化注册用户')
        router.push({path: '/init'})
      }
    })
    .catch((r) => {
      console.error(r)
      ElMessage.error('Oops, network error')
    })

fetch(import.meta.env.VITE_API_BASE_URL + 'system', {
  method: 'get',
  headers: {
    'Content-Type': 'application/json'
  },
  credentials: 'include'
})
    .then((r) => r.json())
    .then((res) => console.log(res))
</script>

<template>
  <main>main 123</main>
</template>
