<script lang="ts" setup>
import router from '@/router'
import type {ResultVO} from '@/type/ResultVO'
import type {FormInstance, FormRules} from 'element-plus'
import {reactive, ref} from 'vue'

interface RuleForm {
    name: string
    password: string
}

const user = reactive<RuleForm>({
    name: 'username',
    password: ''
})

const loading = ref(false)
const ruleFormRef = ref<FormInstance>()

const rules = reactive<FormRules<RuleForm>>({
    name: [
        {required: true, message: '请输入用户名', trigger: 'blur'},
        {min: 3, max: 15, message: '长度应为 3 到 15 个字符', trigger: 'blur'}
    ],
    password: [
        {required: true, message: '请输入密码', trigger: 'blur'},
        {min: 5, max: 15, message: '长度应为 5 到 15 个字符', trigger: 'blur'}
    ]
})

const submitForm = async (formEl: FormInstance | undefined) => {
    if (!formEl) {
        return
    }
    await formEl.validate(async (valid) => {
        if (!valid) {
            return
        }
        loading.value = true
        try {
            const response = await fetch(import.meta.env.VITE_API_BASE_URL + 'user/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                cache: 'no-cache',
                credentials: 'include',
                body: JSON.stringify(user)
            })
            const res = await response.json() as ResultVO<boolean>
            if (res && res.code === '00000' && res.data) {
                ElMessage.success('登录成功')
                router.push({path: '/'})
                return
            }
            ElMessage.error(res.msg || '登录失败')
        } catch (error) {
            console.error(error)
            ElMessage.error('无法连接后端服务')
        } finally {
            loading.value = false
        }
    })
}

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) {
        return
    }
    formEl.resetFields()
}
</script>

<template>
  <div class="auth-page">
    <el-form ref="ruleFormRef" :model="user" :rules="rules" class="auth-form" label-position="top" status-icon>
      <div class="auth-title">
        <h1>MoLiAPI</h1>
        <span>后台管理登录</span>
      </div>
      <el-form-item label="用户名" prop="name">
        <el-input v-model="user.name" placeholder="请输入用户名"/>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input
            v-model="user.password"
            placeholder="请输入密码"
            show-password
            type="password"
            @keyup.enter="submitForm(ruleFormRef)"
        />
      </el-form-item>
      <div class="form-actions">
        <el-button :loading="loading" type="primary" @click="submitForm(ruleFormRef)">登录</el-button>
        <el-button @click="resetForm(ruleFormRef)">重置</el-button>
      </div>
    </el-form>
  </div>
</template>

<style scoped>
.auth-page {
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 100vh;
    padding: 24px;
    background: #f5f7fb;
}

.auth-form {
    width: min(100%, 360px);
    padding: 26px;
    background: #ffffff;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
}

.auth-title {
    margin-bottom: 22px;
}

.auth-title h1 {
    margin: 0;
    color: #111827;
    font-size: 24px;
    font-weight: 700;
}

.auth-title span {
    color: #6b7280;
}

.form-actions {
    display: flex;
    gap: 10px;
    justify-content: flex-end;
}
</style>
