<template>
  <el-card>
    <div class="toolbar">
      <el-select v-model="parentId" placeholder="选择父类型" style="width: 280px" @change="load()">
        <el-option v-for="t in rootTypes" :key="t.id" :label="t.name" :value="t.id" />
      </el-select>
      <el-button type="primary" @click="openDialog()"><el-icon><Plus /></el-icon>新增类型</el-button>
    </div>
    <el-table :data="types" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="类型名称" />
      <el-table-column prop="uri" label="标识" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="onDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑API类型' : '新增API类型'" width="480px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="父类型">
          <el-select v-model="form.api_Type" style="width: 100%">
            <el-option v-for="t in rootTypes" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型名称" required>
          <el-input v-model="form.interface_name" placeholder="如：数据服务" />
        </el-form-item>
        <el-form-item label="标识" required>
          <el-input v-model="form.url_val" placeholder="如：data_service" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.state">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="onSave">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getApiTypes, getApiList, addApiResource, updateApiResource, deleteApiResource } from '../api'

const rootTypes = ref([])
const types = ref([])
const parentId = ref(null)
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const form = ref({ id: null, api_Type: null, interface_name: '', url_val: '', state: 1 })

// 根类型列表（parent_id=-1 的分类集合）
async function loadRootTypes() {
  try {
    const data = await getApiTypes()
    rootTypes.value = Array.isArray(data) ? data : []
    if (rootTypes.value.length && parentId.value === null) {
      parentId.value = rootTypes.value[0].id
    }
  } catch (e) {
    ElMessage.error(e.message)
  }
}

// 当前父类型下的子类型列表
async function load() {
  if (parentId.value === null) return
  loading.value = true
  try {
    const data = await getApiList(String(parentId.value), '1', '100')
    if (data && data.list) {
      types.value = data.list.filter(t => Number(t.type) === 3)
    } else {
      types.value = []
    }
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}

function openDialog(row) {
  if (row) {
    form.value = {
      id: row.id,
      api_Type: row.parentId,
      interface_name: row.name,
      url_val: row.uri,
      state: Number(row.status)
    }
  } else {
    form.value = { id: null, api_Type: parentId.value, interface_name: '', url_val: '', state: 1 }
  }
  dialogVisible.value = true
}

async function onSave() {
  if (!form.value.interface_name || !form.value.url_val || !form.value.api_Type) {
    ElMessage.warning('请填写完整')
    return
  }
  saving.value = true
  try {
    // 类型：sour_Type=3(分类), api_Type=父分类id, type=3 与根类型110/子类型103一致
    const data = {
      interface_name: form.value.interface_name,
      url_val: form.value.url_val,
      api_Type: String(form.value.api_Type),
      sour_Type: '3',
      request_method: 'NONE',
      state: String(form.value.state),
      isAuth: '0'
    }
    if (form.value.id) {
      await updateApiResource(data, String(form.value.id))
      ElMessage.success('更新成功')
    } else {
      await addApiResource(data)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    load()
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    saving.value = false
  }
}

async function onDelete(row) {
  await ElMessageBox.confirm(`确定删除类型「${row.name}」？`, '提示', { type: 'warning' })
  try {
    await deleteApiResource(String(row.id))
    ElMessage.success('删除成功')
    load()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

onMounted(async () => {
  await loadRootTypes()
  load()
})
</script>

<style scoped>
.toolbar { display: flex; gap: 12px; margin-bottom: 12px; }
</style>
