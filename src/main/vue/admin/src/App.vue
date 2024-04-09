<script lang="ts" setup>
import {ElConfigProvider} from 'element-plus'
import 'dayjs/locale/zh-cn'
import zhCn from 'element-plus/es/locale/lang/zh-cn.mjs'
</script>

<template>
  <Suspense>
    <el-config-provider :locale="zhCn">
      <RouterView/>
    </el-config-provider>
  </Suspense>
</template>

<style></style>
