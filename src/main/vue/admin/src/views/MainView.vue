<script lang="ts" setup>
import router from '@/router'
import type {ResultVO} from '@/type/ResultVO'
import type {FormInstance} from 'element-plus'
import {reactive, ref} from 'vue'

let flag = false;
fetch(import.meta.env.VITE_API_BASE_URL + 'user/init', {
  method: 'get',
  headers: {
    'Content-Type': 'application/json'
  }
})
    .then((r) => r.json() as Promise<ResultVO<Boolean>>)
    .then((res) => {
      if (res && res.code === '00000' && res.data === true) {
        ElMessage('前往初始化注册用户')
        flag = true;
        router.push({path: '/init'})
      }
    })
    .catch((r) => {
      console.error(r)
      ElMessage.error('Oops, network error')
    })

function getSystemConfig() {
  fetch(import.meta.env.VITE_API_BASE_URL + 'system', {
    method: 'get',
    headers: {
      'Content-Type': 'application/json'
    }
  })
      .then((r) => {
        if (r.status === 200 && r.ok) {
          return r.json() as Promise<ResultVO<ApiConfig>>
        } else {
          if (r.status === 401) {
            if (!flag) {
              router.push({path: '/login'});
          }
          }
        }
      })
      .then((res) => {
        if (res && res.code === '00000') {
          form = Object.assign(form, res.data)
        }
      })
      .catch((r) => {
        console.log(r)
      })
}

if (!flag) {
  getSystemConfig()
}

interface ApiConfig {
  weatherKey: string
  weatherUri: string
  weatherGeoUri: string
  serverSauceKey: string
  pcPath: string
  mobilePath: string
  path1080: string
  uploadTempPath: string
  uploadTempSecret: string
  certificatePath: string
  privateKeyPath: string
  domain: string
  playListId: Array<string>
  cookies: string
}

let form = reactive<ApiConfig>({
  weatherKey: '',
  weatherUri: '',
  weatherGeoUri: '',
  serverSauceKey: '',
  pcPath: '',
  mobilePath: '',
  path1080: '',
  uploadTempPath: '',
  uploadTempSecret: '',
  certificatePath: '',
  privateKeyPath: '',
  domain: '',
  playListId: [],
  cookies: ''
})
const ruleFormRef = ref<FormInstance>()
const resetForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  getSystemConfig()
}
</script>

<template>
  <main>
    <el-form ref="ruleFormRef" :model="form" status-icon>
      <el-form-item label="和风天气key" prop="weatherKey">
        <el-input v-model="form.weatherKey" placeholder="Please input weatherKey"/>
      </el-form-item>
      <el-form-item label="和风天气请求uri" prop="weatherUri">
        <el-input v-model="form.weatherUri" placeholder="Please input weatherUri"/>
      </el-form-item>
      <el-form-item label="和风天气请求地区ApiUri" prop="weatherGeoUri">
        <el-input v-model="form.weatherGeoUri" placeholder="Please input weatherGeoUri"/>
      </el-form-item>
      <el-form-item label="server酱key" prop="serverSauceKey">
        <el-input v-model="form.serverSauceKey" placeholder="Please input serverSauceKey"/>
      </el-form-item>
      <el-form-item label="pc图片路径" prop="pcPath">
        <el-input v-model="form.pcPath" placeholder="Please input pcPath"/>
      </el-form-item>
      <el-form-item label="mobile图片路径" prop="mobilePath">
        <el-input v-model="form.mobilePath" placeholder="Please input mobilePath"/>
      </el-form-item>
      <el-form-item label="1080p" prop="path1080">
        <el-input v-model="form.path1080" placeholder="Please input path1080"/>
      </el-form-item>
      <el-form-item label="上传临时文件的路径" prop="uploadTempPath">
        <el-input v-model="form.uploadTempPath" placeholder="Please input uploadTempPath"/>
      </el-form-item>
      <el-form-item label="上传临时文件的密钥" prop="uploadTempSecret">
        <el-input v-model="form.uploadTempSecret" placeholder="Please input uploadTempSecret"/>
      </el-form-item>
      <el-form-item label="证书路径(为腾讯CDN自动更新)" prop="certificatePath">
        <el-input v-model="form.certificatePath" placeholder="Please input certificatePath"/>
      </el-form-item>
      <el-form-item label="私钥路径(为腾讯CDN自动更新)" prop="password">
        <el-input v-model="form.privateKeyPath" placeholder="Please input privateKeyPath"/>
      </el-form-item>
      <el-form-item label="域名" prop="domain">
        <el-input v-model="form.domain" placeholder="Please input domain"/>
      </el-form-item>
      <!-- <el-form-item label="定时任务歌单id" prop="playListId"> -->
      <!-- <el-input v-model="form.playListId" placeholder="Please input playListId"/> -->
      <!-- </el-form-item> -->
      <el-form-item label="歌单用的cookies，" prop="cookies">
        <el-input v-model="form.cookies" placeholder="Please input cookies"/>
      </el-form-item>

      <el-form-item>
        <!-- <el-button type="primary" @click="submitForm(ruleFormRef)">Login</el-button> -->
        <el-button @click="resetForm(ruleFormRef)">Reset</el-button>
      </el-form-item>
    </el-form>
  </main>
</template>
