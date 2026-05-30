<script lang="ts" setup>
import type {DailyMap} from '@/type/Daily'
import {get, post, put} from '@/util/request'
import type {CalendarInstance, FormInstance, FormRules} from 'element-plus'
import {computed, onMounted, reactive, ref, watch} from 'vue'
// @ts-ignore
import calendar from '@/util/lunnerDay'

interface DailyParam {
    startDate?: string
    endDate?: string
    urls?: string[]
}

interface DailySiteForm {
    key: string
    value: string
}

const tenant = ref('moli')
const loading = ref(false)
const signing = ref(false)
const adding = ref(false)
const signed = ref(true)
const selectedUrls = ref<string[]>([])
const options = ref<string[]>([])
const month = ref<Date>(new Date())
const calendarInstance = ref<CalendarInstance>()
const dailyMap = ref<DailyMap>({})
const siteFormRef = ref<FormInstance>()

const siteForm = reactive<DailySiteForm>({
    key: 'moli',
    value: ''
})

const siteRules = reactive<FormRules<DailySiteForm>>({
    key: [{required: true, message: '请输入分组 Key', trigger: 'blur'}],
    value: [{required: true, message: '请输入签到网址', trigger: 'blur'}]
})

const signedDays = computed(() => Object.keys(dailyMap.value).length)

const formatDate = (date: Date) => {
    const year = date.getFullYear()
    const monthValue = `${date.getMonth() + 1}`.padStart(2, '0')
    const day = `${date.getDate()}`.padStart(2, '0')
    return `${year}-${monthValue}-${day}`
}

const buildQueryUrl = () => {
    const date = new Date(month.value)
    const firstDayOfMonth = new Date(date.getFullYear(), date.getMonth(), 1)
    const lastDayOfMonth = new Date(date.getFullYear(), date.getMonth() + 1, 0)
    const params = new URLSearchParams({
        startDate: formatDate(firstDayOfMonth),
        endDate: formatDate(lastDayOfMonth)
    })
    selectedUrls.value.forEach((item) => params.append('urls', item))
    return `daily/query?${params.toString()}`
}

const loadCheckStatus = async () => {
    const signedUrls = await get<string[]>('daily/check', {
        headers: {
            tenant: tenant.value
        }
    })
    signed.value = signedUrls.length > 0
}

const loadParam = async () => {
    const param = await get<DailyParam>('daily/param', {
        headers: {
            tenant: tenant.value
        }
    })
    options.value = param?.urls ?? []
}

const query = async () => {
    dailyMap.value = await get<DailyMap>(buildQueryUrl(), {
        headers: {
            tenant: tenant.value
        }
    })
}

const loadPage = async () => {
    loading.value = true
    try {
        await Promise.all([loadCheckStatus(), loadParam(), query()])
    } catch (error) {
        console.error(error)
        ElMessage.error('读取签到数据失败')
    } finally {
        loading.value = false
    }
}

const sign = async () => {
    signing.value = true
    try {
        const dailyList = await get<string[]>(`daily?key=${tenant.value}`)
        dailyList.forEach((item) => window.open(item, '_blank'))
        const signResult = await post<boolean>('daily/signed', dailyList)
        if (signResult.code === '00000' && signResult.data) {
            ElMessage.success('签到记录已保存')
            await loadPage()
            return
        }
        ElMessage.error(signResult.msg || '签到记录保存失败')
    } catch (error) {
        console.error(error)
        ElMessage.error('签到失败')
    } finally {
        signing.value = false
    }
}

const addDailySite = async (formEl: FormInstance | undefined) => {
    if (!formEl) {
        return
    }
    await formEl.validate(async (valid) => {
        if (!valid) {
            return
        }
        adding.value = true
        try {
            const res = await put<string[]>('daily', siteForm)
            if (res.code === '00000') {
                ElMessage.success('签到网址已添加')
                siteForm.value = ''
                tenant.value = siteForm.key
                await loadPage()
                return
            }
            ElMessage.error(res.msg || '签到网址添加失败')
        } catch (error) {
            console.error(error)
            ElMessage.error('签到网址添加失败')
        } finally {
            adding.value = false
        }
    })
}

const randomMusic = () => {
    window.open(`${import.meta.env.VITE_API_BASE_URL}music/random`, '_blank')
}

onMounted(loadPage)

watch(month, () => {
    query().catch((error) => {
        console.error(error)
        ElMessage.error('刷新签到日历失败')
    })
})
</script>

<template>
  <section class="daily-page" v-loading="loading">
    <div class="toolbar">
      <div class="stat">
        <span>本月已签到</span>
        <strong>{{ signedDays }}</strong>
      </div>
      <div class="toolbar-actions">
        <el-input v-model="tenant" class="tenant-input" placeholder="tenant" @change="loadPage"/>
        <el-button :disabled="signed" :loading="signing" type="success" @click="sign">今日签到</el-button>
        <el-button @click="randomMusic">随机音乐</el-button>
      </div>
    </div>

    <div class="management-grid">
      <section class="panel">
        <div class="panel-header">
          <h2>签到网址</h2>
          <span>添加后会进入当前内存签到列表</span>
        </div>
        <el-form ref="siteFormRef" :model="siteForm" :rules="siteRules" label-position="top">
          <el-form-item label="分组 Key" prop="key">
            <el-input v-model="siteForm.key" placeholder="moli"/>
          </el-form-item>
          <el-form-item label="网址" prop="value">
            <el-input v-model="siteForm.value" placeholder="https://example.com"/>
          </el-form-item>
          <el-button :loading="adding" type="primary" @click="addDailySite(siteFormRef)">添加网址</el-button>
        </el-form>
      </section>

      <section class="panel filter-panel">
        <div class="panel-header">
          <h2>查询条件</h2>
          <span>按月份和网址查看签到记录</span>
        </div>
        <el-form label-position="top">
          <el-form-item label="网址">
            <el-select
                v-model="selectedUrls"
                clearable
                multiple
                placeholder="全部网址"
                @change="query"
            >
              <el-option v-for="item in options" :key="item" :label="item" :value="item"/>
            </el-select>
          </el-form-item>
          <el-form-item label="月份">
            <el-date-picker v-model="month" placeholder="选择月份" type="month" @change="query"/>
          </el-form-item>
        </el-form>
      </section>
    </div>

    <section class="calendar-panel">
      <el-calendar ref="calendarInstance" v-model="month">
        <template #date-cell="{ data }">
          <div
              :class="{
                  selected: data.isSelected,
                  signed: dailyMap[data.day],
                  missed: !dailyMap[data.day] && new Date(data.day) < new Date()
              }"
              class="day-cell"
          >
            <span class="solar-day">{{ data.day.split('-')[2] }}</span>
            <span class="lunar-day">
              {{
                calendar.solar2lunar(
                    data.day.split('-')[0],
                    data.day.split('-')[1],
                    data.day.split('-')[2]
                ).IDayCn
              }}
            </span>
            <span v-if="dailyMap[data.day]" class="day-status">已签</span>
          </div>
        </template>
      </el-calendar>
    </section>
  </section>
</template>

<style scoped>
.daily-page {
    display: flex;
    flex-direction: column;
    gap: 18px;
}

.toolbar,
.panel,
.calendar-panel {
    background: #ffffff;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
}

.toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16px;
    padding: 18px 20px;
}

.stat {
    display: flex;
    align-items: baseline;
    gap: 10px;
}

.stat span {
    color: #6b7280;
}

.stat strong {
    color: #111827;
    font-size: 26px;
    font-weight: 700;
}

.toolbar-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    justify-content: flex-end;
}

.tenant-input {
    width: 160px;
}

.management-grid {
    display: grid;
    grid-template-columns: minmax(260px, 360px) minmax(320px, 1fr);
    gap: 18px;
}

.panel {
    padding: 18px;
}

.panel-header {
    margin-bottom: 16px;
}

.panel-header h2 {
    margin: 0;
    color: #111827;
    font-size: 16px;
    font-weight: 700;
}

.panel-header span {
    color: #6b7280;
    font-size: 13px;
}

.filter-panel :deep(.el-select),
.filter-panel :deep(.el-date-editor) {
    width: 100%;
}

.calendar-panel {
    padding: 12px;
}

.day-cell {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    width: 100%;
    height: 100%;
    min-height: 86px;
    border-radius: 6px;
}

.solar-day {
    color: #111827;
    font-size: 24px;
    font-weight: 700;
}

.lunar-day,
.day-status {
    color: #6b7280;
    font-size: 12px;
}

.signed {
    background: #ecfdf5;
}

.signed .solar-day,
.signed .day-status {
    color: #059669;
}

.missed {
    background: #fef2f2;
}

.missed .solar-day {
    color: #dc2626;
}

.selected {
    outline: 2px solid #409eff;
}

@media (max-width: 920px) {
    .toolbar {
        align-items: flex-start;
        flex-direction: column;
    }

    .toolbar-actions {
        justify-content: flex-start;
        width: 100%;
    }

    .management-grid {
        grid-template-columns: 1fr;
    }
}
</style>
