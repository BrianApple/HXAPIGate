<template>
  <el-card>
    <div class="toolbar">
      <el-select v-model="typeId" placeholder="全部类型" style="width: 200px" @change="load(1)">
        <el-option label="全部类型" :value="-1" />
        <el-option v-for="t in types" :key="t.id" :label="t.name" :value="t.id" />
      </el-select>
      <el-button type="primary" @click="openDialog()"><el-icon><Plus /></el-icon>新增API</el-button>
    </div>

    <el-table :data="apis" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="name" label="接口名称" min-width="140" />
      <el-table-column prop="uri" label="URI" min-width="160" />
      <el-table-column prop="method" label="方法" width="90">
        <template #default="{ row }"><el-tag size="small">{{ row.method }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="version" label="版本" width="80" />
      <el-table-column label="协议" width="90">
        <template #default="{ row }">
          <el-tag size="small" :type="protoType(row) === 'mcp' ? 'warning' : protoType(row) === 'tcp' ? 'danger' : ''">
            {{ protoType(row) === 'dubbo' ? 'Dubbo' : protoType(row) === 'tcp' ? 'TCP' : protoType(row) === 'mcp' ? 'MCP' : 'HTTP' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="onDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      class="pager" background layout="total, prev, pager, next"
      :total="total" :page-size="pageSize" :current-page="pageIndex"
      @current-change="load" />
    <ApiEditDialog ref="dialogRef" @saved="load()" />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getApiTypes, getApiList, getApiDetail, addApiResource, updateApiResource, deleteApiResource } from '../api'
import ApiEditDialog from './ApiEditDialog.vue'

const types = ref([])
const apis = ref([])
const typeId = ref(-1)
const pageIndex = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)
const dialogRef = ref(null)

async function load(page = pageIndex.value) {
  pageIndex.value = page
  loading.value = true
  try {
    const data = await getApiList(String(typeId.value), String(page), String(pageSize.value))
    if (data && data.list) {
      apis.value = data.list
      total.value = Number(data.total || 0)
    } else {
      apis.value = []
      total.value = 0
    }
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}

function openDialog(row) {
  dialogRef.value.open(row || null)
}

// 从 routeInfo JSON 解析代理协议类型（默认 http）
function protoType(row) {
  try {
    const info = row.routeInfo ? JSON.parse(row.routeInfo) : null
    return (info && info.pType) || 'http'
  } catch {
    return 'http'
  }
}

async function onDelete(row) {
  await ElMessageBox.confirm(`确定删除API「${row.name}」？`, '提示', { type: 'warning' })
  try {
    await deleteApiResource(String(row.id))
    ElMessage.success('删除成功')
    load()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

onMounted(async () => {
  try {
    const data = await getApiTypes()
    types.value = Array.isArray(data) ? data : []
  } catch {}
  load(1)
})

defineExpose({ refresh: () => load() })
</script>

<style scoped>
.toolbar { display: flex; gap: 12px; margin-bottom: 12px; }
.pager { margin-top: 12px; justify-content: flex-end; }
</style>
