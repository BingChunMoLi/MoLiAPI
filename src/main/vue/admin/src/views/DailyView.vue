<script async lang="ts" setup>
import {get, post} from "@/util/request";
import {ref} from "vue";

const res = await get('daily/check')
const signed = ref(true)
if (res.length == 0) {
  signed.value = false
}
//res 当日签到的数组 长度为0时未签到
const param = await get('daily/param');
console.log(param)

const sign = async () => {
  const dailyList = await get('daily?key=moli')
  for (let i of dailyList) {
    window.open(i, '_blank');
  }
  const signResult = await post('daily/signed', dailyList)
  console.log("签到结果: ", signResult ? "成功" : "失败");
}

</script>

<template>
  <div style="width: 100%; height: 100vh">
    <el-calendar>
      <template #date-cell="{ data }">
        <p :class="data.isSelected ? 'is-selected' : ''">
          {{ data.day.split('-').slice(1).join('-') }}
          {{ data.isSelected ? '✔️' : '' }}
        </p>
      </template>
      <template #header="{date}">
        {{ date }}
        <el-button :disabled="signed" round type="success" @click="sign">签到</el-button>
      </template>
    </el-calendar>
  </div>
</template>

<style scoped>

</style>
