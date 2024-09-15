<script async lang="ts" setup>
import {get, post} from "@/util/request";
import {ref} from "vue";

const res = await get('daily/check')
const signed = ref(true)
if (res.length == 0) {
  signed.value = false
}
//res 当日签到的数组 长度为0时未签到
const url = ref('')
const param = await get('daily/param');
const month = ref<Date>(new Date())
const options = ref<Array<string>>()
if (param) {
  options.value = [...param.urls]
}else{
  options.value = ["testa", "testb"]
}
const sign = async () => {
  const dailyList = await get('daily?key=moli')
  for (let i of dailyList) {
    window.open(i, '_blank');
  }
  const signResult = await post('daily/signed', dailyList)
  console.log("签到结果: ", signResult ? "成功" : "失败");
}
const change = async () => {
  const date = new Date(month.value);
  const firstDayOfMonth = new Date(date.getFullYear(), date.getMonth(), 1)
  const lastDayOfMonth = new Date(date.getFullYear(), date.getMonth() + 1, 0);
  let requestUrl = 'daily/query?startDate=' + firstDayOfMonth.toLocaleDateString() +'&endDate=' + lastDayOfMonth.toLocaleDateString();
  if (url.value) {
    requestUrl = requestUrl + '&urls=' + url.value
  }
  const daily = await get(requestUrl);
  console.log(daily)
};
</script>

<template>
  <div style="width: 100%; height: 100vh">
    <el-calendar>
      <template #date-cell="{ data }">
        <p :class="data.isSelected ? 'is-selected' : ''">
          {{ data.day.split('-').slice(1).join('-') }}
          {{ data.isSelected ? '✔️' : '' }}
          {{ data.day }}
        </p>
      </template>
      <template #header="{date}">
        {{ date }}
        <el-select
            v-model="url"
            clearable
            placeholder="Select"
            size="large"
            style="width: 240px"
            @change="change"
        >
          <el-option
              v-for="item in options"
              :key="item"
              :label="item"
              :value="item"
          />
        </el-select>
        <div class="block">
          <span class="demonstration">Month</span>
          <el-date-picker
              v-model="month"
              placeholder="Pick a month"
              type="month"
              @change="change"
          />
        </div>
        <el-button :disabled="signed" round type="success" @click="sign">签到</el-button>
      </template>
    </el-calendar>
  </div>
</template>

<style scoped>

</style>
