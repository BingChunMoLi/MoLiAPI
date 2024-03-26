<script lang="ts" setup>
import {ElConfigProvider} from 'element-plus'
import 'dayjs/locale/zh-cn'
import locale from 'element-plus/dist/locale/zh-cn.mjs'
</script>

<template>
  <Suspense>
    <el-config-provider :locale="locale">
      <RouterView/>
    </el-config-provider>
  </Suspense>
</template>

<style></style>
