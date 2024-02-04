<template>
  <div>
    <el-form ref="ruleFormRef" :model="user" :rules="rules" status-icon>
      <el-form-item label="username" prop="name">
        <el-input v-model="user.name" placeholder="Please input username"/>
      </el-form-item>
      <el-form-item label="password" prop="password">
        <el-input
            v-model="user.password"
            placeholder="Please input password"
            show-password
            type="password"
        />
      </el-form-item>
      <el-alert show-icon title="第一次启动, 创建用户初始化" type="info"/>
      <el-form-item>
        <el-button type="primary" @click="submitForm(ruleFormRef)">register</el-button>
        <el-button @click="resetForm(ruleFormRef)">Reset</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script lang="ts" setup>
import {reactive, ref} from 'vue'
import type {FormInstance, FormRules} from 'element-plus'
import {ElMessage} from 'element-plus'
import router from '@/router'

const user = reactive<RuleForm>({
  name: 'username',
  password: ''
})

interface RuleForm {
  name: string
  password: string
}

const ruleFormRef = ref<FormInstance>()

const rules = reactive<FormRules<RuleForm>>({
  name: [
    {required: true, message: 'Please input username', trigger: 'blur'},
    {min: 3, max: 15, message: 'Length should be 3 to 15', trigger: 'blur'}
  ],
  password: [
    {required: true, message: 'Please select password', trigger: 'blur'},
    {min: 5, max: 15, message: 'Length should be 5 to 15', trigger: 'blur'}
  ]
})
const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      fetch(import.meta.env.VITE_API_BASE_URL, {
        method: 'put',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(user)
      })
          .then((r) => r.json())
          .then((res) => {
            console.log(res)
            if (res && res.code === '00000' && res.data === true) {
              router.push({path: '/'})
            } else {
              ElMessage.error('Oops, ' + res.msg)
            }
          })
          .catch((r) => {
            console.error(r)
            ElMessage.error('Oops, network error')
          })
    } else {
      console.log('error submit!')
    }
  })
}
const resetForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.resetFields()
}
</script>

<style scoped></style>
