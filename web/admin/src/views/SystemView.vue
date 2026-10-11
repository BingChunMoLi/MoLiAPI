<script lang="ts" setup>
import router from '@/router'
import type {ApiConfig} from '@/type/ApiConfig'
import {get, post} from '@/util/request'
import type {FormInstance} from 'element-plus'
import {onMounted, reactive, ref} from 'vue'

const loading = ref(false)
const saving = ref(false)
const ruleFormRef = ref<FormInstance>()

const form = reactive<ApiConfig>({
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
    playListId: '',
    cookies: ''
})

const applyConfig = (config: Partial<ApiConfig>) => {
    Object.assign(form, config)
}

const loadConfig = async () => {
    loading.value = true
    try {
        applyConfig(await get<ApiConfig>('system'))
    } catch (error) {
        console.error(error)
        ElMessage.error('读取系统配置失败，请确认登录状态')
        router.push({path: '/login'})
    } finally {
        loading.value = false
    }
}

const resetForm = () => {
    loadConfig()
}

const submitForm = async (formEl: FormInstance | undefined) => {
    if (!formEl) {
        return
    }
    saving.value = true
    try {
        const res = await post<boolean>('system', form)
        if (res.code === '00000' && res.data) {
            ElMessage.success('系统配置已保存')
            return
        }
        ElMessage.error(res.msg || '系统配置保存失败')
    } catch (error) {
        console.error(error)
        ElMessage.error('系统配置保存失败')
    } finally {
        saving.value = false
    }
}

onMounted(loadConfig)
</script>

<template>
  <section class="panel" v-loading="loading">
    <div class="panel-header">
      <div>
        <h2>系统配置</h2>
        <span>维护天气、图片、上传、证书和音乐相关配置</span>
      </div>
      <el-button :loading="loading" @click="resetForm">重新加载</el-button>
    </div>

    <el-form ref="ruleFormRef" :model="form" class="config-form" label-position="top" status-icon>
      <div class="form-grid">
        <el-form-item label="和风天气 Key" prop="weatherKey">
          <el-input v-model="form.weatherKey" placeholder="请输入 weatherKey" show-password/>
        </el-form-item>
        <el-form-item label="天气 API 域名" prop="weatherUri">
          <el-input v-model="form.weatherUri" placeholder="devapi.qweather.com"/>
        </el-form-item>
        <el-form-item label="天气城市 API 域名" prop="weatherGeoUri">
          <el-input v-model="form.weatherGeoUri" placeholder="geoapi.qweather.com"/>
        </el-form-item>
        <el-form-item label="Server 酱 Key" prop="serverSauceKey">
          <el-input v-model="form.serverSauceKey" placeholder="请输入 serverSauceKey" show-password/>
        </el-form-item>
        <el-form-item label="PC 图片路径" prop="pcPath">
          <el-input v-model="form.pcPath" placeholder="请输入 pcPath"/>
        </el-form-item>
        <el-form-item label="Mobile 图片路径" prop="mobilePath">
          <el-input v-model="form.mobilePath" placeholder="请输入 mobilePath"/>
        </el-form-item>
        <el-form-item label="1080P 图片路径" prop="path1080">
          <el-input v-model="form.path1080" placeholder="请输入 path1080"/>
        </el-form-item>
        <el-form-item label="上传临时目录" prop="uploadTempPath">
          <el-input v-model="form.uploadTempPath" placeholder="请输入 uploadTempPath"/>
        </el-form-item>
        <el-form-item label="上传临时密钥" prop="uploadTempSecret">
          <el-input v-model="form.uploadTempSecret" placeholder="请输入 uploadTempSecret" show-password/>
        </el-form-item>
        <el-form-item label="腾讯 CDN 证书路径" prop="certificatePath">
          <el-input v-model="form.certificatePath" placeholder="请输入 certificatePath"/>
        </el-form-item>
        <el-form-item label="腾讯 CDN 私钥路径" prop="privateKeyPath">
          <el-input v-model="form.privateKeyPath" placeholder="请输入 privateKeyPath"/>
        </el-form-item>
        <el-form-item label="证书域名" prop="domain">
          <el-input v-model="form.domain" placeholder="请输入 domain"/>
        </el-form-item>
        <el-form-item label="网易云歌单 ID" prop="playListId">
          <el-input v-model="form.playListId" placeholder="请输入 playListId"/>
        </el-form-item>
      </div>

      <el-form-item label="歌单 Cookie" prop="cookies">
        <el-input v-model="form.cookies" :rows="4" placeholder="请输入 cookies" show-password type="textarea"/>
      </el-form-item>

      <div class="form-actions">
        <el-button :loading="saving" type="primary" @click="submitForm(ruleFormRef)">保存配置</el-button>
        <el-button @click="resetForm">重置</el-button>
      </div>
    </el-form>
  </section>
</template>

<style scoped>
.panel {
    padding: 24px;
    background: #ffffff;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
}

.panel-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16px;
    margin-bottom: 22px;
}

.panel-header h2 {
    margin: 0;
    color: #111827;
    font-size: 18px;
    font-weight: 700;
}

.panel-header span {
    color: #6b7280;
    font-size: 13px;
}

.form-grid {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 4px 18px;
}

.form-actions {
    display: flex;
    gap: 12px;
    justify-content: flex-end;
    margin-top: 12px;
}

@media (max-width: 1080px) {
    .form-grid {
        grid-template-columns: repeat(2, minmax(0, 1fr));
    }
}

@media (max-width: 720px) {
    .panel-header {
        align-items: flex-start;
        flex-direction: column;
    }

    .form-grid {
        grid-template-columns: 1fr;
    }
}
</style>
