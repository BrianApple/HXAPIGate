<template>
  <el-card>
    <div class="toolbar">
      <el-button type="primary" @click="openDialog()"><el-icon><Plus /></el-icon>新增角色</el-button>
    </div>
    <el-table :data="roles" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="角色名称" />
      <el-table-column prop="code" label="角色编码" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button link type="warning" @click="$router.push(`/roleAuth/${row.id}`)">授权</el-button>
          <el-button link type="danger" @click="onDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      class="pager" background layout="total, prev, pager, next"
      :total="total" :page-size="pageSize" :current-page="pageIndex"
      @current-change="load" />

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑角色' : '新增角色'" width="440px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="角色名称" required>
          <el-input v-model="form.role_name" placeholder="如：管理员" />
        </el-form-item>
        <el-form-item label="角色编码" required>
          <el-input v-model="form.role_code" placeholder="如：role_admin" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.role_state">
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
import { getRoleList, addRole, updateRoleById, deleteRoleById } from '../api'

const roles = ref([])
const total = ref(0)
const pageIndex = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const form = ref({ id: null, role_name: '', role_code: '', role_state: 1 })

async function load(page = pageIndex.value) {
  pageIndex.value = page
  loading.value = true
  try {
    const data = await getRoleList(String(page), String(pageSize.value))
    if (data && data.list) {
      roles.value = data.list
      total.value = Number(data.total || 0)
    } else {
      roles.value = []
      total.value = 0
    }
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}

function openDialog(row) {
  if (row) {
    form.value = { id: row.id, role_name: row.name, role_code: row.code, role_state: Number(row.status) }
  } else {
    form.value = { id: null, role_name: '', role_code: '', role_state: 1 }
  }
  dialogVisible.value = true
}

async function onSave() {
  if (!form.value.role_name || !form.value.role_code) {
    ElMessage.warning('请填写完整')
    return
  }
  saving.value = true
  try {
    const data = {
      role_name: form.value.role_name,
      role_code: form.value.role_code,
      role_state: String(form.value.role_state)
    }
    if (form.value.id) {
      await updateRoleById(data, String(form.value.id))
      ElMessage.success('更新成功')
    } else {
      await addRole(data)
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
  await ElMessageBox.confirm(`确定删除角色「${row.name}」？`, '提示', { type: 'warning' })
  try {
    await deleteRoleById(String(row.id))
    ElMessage.success('删除成功')
    load()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

onMounted(() => load(1))
</script>

<style scoped>
.toolbar { margin-bottom: 12px; }
.pager { margin-top: 12px; justify-content: flex-end; }
</style>
