<script lang="ts" setup>
import type {Navigation, Tag} from '@/type/Navigation'
import {get, post, put, remove} from '@/util/request'
import {ElMessageBox, type FormInstance, type FormRules} from 'element-plus'
import {computed, onMounted, reactive, ref} from 'vue'

const defaultNavigation = (): Navigation => ({
    title: '',
    des: '',
    url: '',
    icon: '',
    tenant: tenant.value || 'public',
    tagList: [defaultTag()]
})

const defaultTag = (): Tag => ({
    tagName: '',
    isOpen: true,
    isPrivate: false,
    pwd: ''
})

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const tenant = ref('public')
const keyword = ref('')
const navigations = ref<Navigation[]>([])
const formRef = ref<FormInstance>()
const editingId = ref<number>()

const form = reactive<Navigation>(defaultNavigation())

const rules = reactive<FormRules<Navigation>>({
    title: [{required: true, message: '请输入标题', trigger: 'blur'}],
    url: [{required: true, message: '请输入网址', trigger: 'blur'}],
    tenant: [{required: true, message: '请输入 tenant', trigger: 'blur'}]
})

const filteredNavigations = computed(() => {
    const value = keyword.value.trim().toLowerCase()
    if (!value) {
        return navigations.value
    }
    return navigations.value.filter((item) => {
        const tags = item.tagList?.map((tag) => tag.tagName).join(' ') ?? ''
        return `${item.title} ${item.des} ${item.url} ${tags}`.toLowerCase().includes(value)
    })
})

const resetFormModel = (navigation?: Navigation) => {
    const source = navigation ?? defaultNavigation()
    Object.assign(form, {
        ...source,
        tagList: source.tagList?.length ? source.tagList.map((tag) => ({...tag})) : [defaultTag()]
    })
}

const loadNavigations = async () => {
    loading.value = true
    try {
        navigations.value = await get<Navigation[]>('navigation', {
            headers: {
                tenant: tenant.value
            }
        })
    } catch (error) {
        console.error(error)
        ElMessage.error('读取导航列表失败')
    } finally {
        loading.value = false
    }
}

const openCreateDialog = () => {
    editingId.value = undefined
    resetFormModel()
    dialogVisible.value = true
}

const openEditDialog = (navigation: Navigation) => {
    editingId.value = navigation.id
    resetFormModel(navigation)
    dialogVisible.value = true
}

const addTag = () => {
    form.tagList = [...(form.tagList ?? []), defaultTag()]
}

const removeTag = (index: number) => {
    if ((form.tagList?.length ?? 0) <= 1) {
        ElMessage.warning('至少保留一个标签')
        return
    }
    form.tagList = form.tagList?.filter((_, itemIndex) => itemIndex !== index)
}

const saveNavigation = async (formEl: FormInstance | undefined) => {
    if (!formEl) {
        return
    }
    await formEl.validate(async (valid) => {
        if (!valid) {
            return
        }
        const validTags = (form.tagList ?? []).filter((tag) => tag.tagName.trim())
        if (!validTags.length) {
            ElMessage.warning('请至少填写一个标签')
            return
        }
        saving.value = true
        try {
            const payload: Navigation = {
                ...form,
                tagList: validTags
            }
            const res = editingId.value
                ? await post<boolean>(`navigation/${editingId.value}`, payload)
                : await put<boolean>('navigation', payload)
            if (res.code === '00000' && res.data) {
                ElMessage.success(editingId.value ? '导航已更新' : '导航已添加')
                dialogVisible.value = false
                await loadNavigations()
                return
            }
            ElMessage.error(res.msg || '保存导航失败')
        } catch (error) {
            console.error(error)
            ElMessage.error('保存导航失败')
        } finally {
            saving.value = false
        }
    })
}

const deleteNavigation = async (navigation: Navigation) => {
    if (!navigation.id) {
        return
    }
    try {
        await ElMessageBox.confirm(`确认删除「${navigation.title}」吗？`, '删除导航', {
            confirmButtonText: '删除',
            cancelButtonText: '取消',
            type: 'warning'
        })
        const res = await remove<boolean>(`navigation/${navigation.id}`)
        if (res.code === '00000' && res.data) {
            ElMessage.success('导航已删除')
            await loadNavigations()
            return
        }
        ElMessage.error(res.msg || '删除导航失败')
    } catch (error) {
        if (error !== 'cancel') {
            console.error(error)
        }
    }
}

const exportNavigation = async () => {
    try {
        const data = await get<Navigation[]>('navigation/export')
        const blob = new Blob([JSON.stringify(data, null, 2)], {type: 'application/json'})
        const url = URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `navigation-${tenant.value}.json`
        link.click()
        URL.revokeObjectURL(url)
    } catch (error) {
        console.error(error)
        ElMessage.error('导出导航失败')
    }
}

onMounted(loadNavigations)
</script>

<template>
  <section class="navigation-page">
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="tenant" class="tenant-input" placeholder="tenant" @change="loadNavigations"/>
        <el-input v-model="keyword" class="keyword-input" clearable placeholder="搜索标题、网址或标签"/>
      </div>
      <div class="toolbar-actions">
        <el-button :loading="loading" @click="loadNavigations">刷新</el-button>
        <el-button @click="exportNavigation">导出</el-button>
        <el-button type="primary" @click="openCreateDialog">新增导航</el-button>
      </div>
    </div>

    <section class="table-panel">
      <el-table v-loading="loading" :data="filteredNavigations" row-key="id">
        <el-table-column label="标题" min-width="160" prop="title"/>
        <el-table-column label="描述" min-width="220" prop="des" show-overflow-tooltip/>
        <el-table-column label="网址" min-width="260" prop="url" show-overflow-tooltip>
          <template #default="{ row }">
            <el-link :href="row.url" target="_blank" type="primary">{{ row.url }}</el-link>
          </template>
        </el-table-column>
        <el-table-column label="Tenant" prop="tenant" width="120"/>
        <el-table-column label="标签" min-width="180">
          <template #default="{ row }">
            <el-tag v-for="tag in row.tagList" :key="tag.id ?? tag.tagName" class="tag-item" size="small">
              {{ tag.tagName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEditDialog(row)">编辑</el-button>
            <el-button link type="danger" @click="deleteNavigation(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑导航' : '新增导航'" width="720px">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <div class="form-grid">
          <el-form-item label="标题" prop="title">
            <el-input v-model="form.title" placeholder="请输入标题"/>
          </el-form-item>
          <el-form-item label="Tenant" prop="tenant">
            <el-input v-model="form.tenant" placeholder="public"/>
          </el-form-item>
          <el-form-item label="网址" prop="url">
            <el-input v-model="form.url" placeholder="https://example.com"/>
          </el-form-item>
          <el-form-item label="图标" prop="icon">
            <el-input v-model="form.icon" placeholder="图标 URL 或标识"/>
          </el-form-item>
        </div>
        <el-form-item label="描述" prop="des">
          <el-input v-model="form.des" :rows="3" placeholder="请输入描述" type="textarea"/>
        </el-form-item>

        <div class="tag-editor">
          <div class="tag-editor-header">
            <strong>标签</strong>
            <el-button link type="primary" @click="addTag">添加标签</el-button>
          </div>
          <div v-for="(tag, index) in form.tagList" :key="index" class="tag-row">
            <el-input v-model="tag.tagName" placeholder="标签名"/>
            <el-switch v-model="tag.isOpen" active-text="可展开"/>
            <el-switch v-model="tag.isPrivate" active-text="私有"/>
            <el-input v-if="tag.isPrivate" v-model="tag.pwd" placeholder="访问密码" show-password/>
            <el-button link type="danger" @click="removeTag(index)">移除</el-button>
          </div>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button :loading="saving" type="primary" @click="saveNavigation(formRef)">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<style scoped>
.navigation-page {
    display: flex;
    flex-direction: column;
    gap: 18px;
}

.toolbar,
.table-panel {
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

.toolbar-left,
.toolbar-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
}

.tenant-input {
    width: 160px;
}

.keyword-input {
    width: 280px;
}

.table-panel {
    padding: 12px;
}

.tag-item {
    margin-right: 6px;
}

.form-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 0 16px;
}

.tag-editor {
    padding: 14px;
    background: #f9fafb;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
}

.tag-editor-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12px;
}

.tag-row {
    display: grid;
    align-items: center;
    grid-template-columns: minmax(160px, 1fr) 100px 80px minmax(120px, 1fr) 52px;
    gap: 10px;
    margin-bottom: 10px;
}

.tag-row:last-child {
    margin-bottom: 0;
}

@media (max-width: 860px) {
    .toolbar {
        align-items: flex-start;
        flex-direction: column;
    }

    .keyword-input {
        width: 100%;
    }

    .form-grid,
    .tag-row {
        grid-template-columns: 1fr;
    }
}
</style>
