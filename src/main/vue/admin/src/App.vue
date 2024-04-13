<script lang="ts" setup>
import {ElConfigProvider} from 'element-plus'
import 'dayjs/locale/zh-cn'
import zhCn from 'element-plus/es/locale/lang/zh-cn.mjs'
import 'element-plus/theme-chalk/dark/css-vars.css'

const darkMode = window.matchMedia('(prefers-color-scheme: dark)').matches;
if (darkMode) {
  document.documentElement.setAttribute("class", "dark");
}else {
  document.documentElement.removeAttribute("class");
}
</script>

<template>
  <Suspense>
    <el-config-provider :locale="zhCn">
      <RouterView/>
    </el-config-provider>
  </Suspense>
</template>

<style></style>
