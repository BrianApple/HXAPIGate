<template>
  <el-card>
    <div class="toolbar">
      <el-button type="primary" @click="openDialog()"><el-icon><Plus /></el-icon>新增用户</el-button>
    </div>
    <el-table :data="users" v-loading="loading" border stripe>
      <el-table-column prop="uid" label="账号" width="120" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="realName" label="真实姓名" width="120" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="email" label="邮箱" min-width="150" />
      <el-table-column prop="sex" label="性别" width="70">
        <template #default="{ row }">
          {{ row.sex === 1 ? '男' : (row.sex === 2 ? '女' : '-') }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="Number(row.status) === 1 ? 'success' : (Number(row.status) === 2 ? 'warning' : 'info')">
            {{ Number(row.status) === 1 ? '正常' : (Number(row.status) === 2 ? '锁定' : (Number(row.status) === 3 ? '已删除' : '非法')) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="roleNames" label="角色" min-width="150">
        <template #default="{ row }">
          {{ row.roleNames || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="230" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button link type="warning" @click="openResetPwd(row)">重置密码</el-button>
          <el-button link type="danger" @click="onDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      class="pager" background layout="total, prev, pager, next"
      :total="total" :page-size="pageSize" :current-page="pageIndex"
      @current-change="load" />

    <el-dialog v-model="dialogVisible" :title="form.uid ? '编辑用户' : '新增用户'" width="560px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="账号" required>
          <el-input v-model="form.uid" :disabled="!!editingUid" placeholder="登录账号，如 zhangsan" />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="昵称" />
        </el-form-item>
        <el-form-item v-if="!editingUid" label="密码" required>
          <el-input v-model="form.password" type="password" show-password placeholder="初始密码" />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.sex">
            <el-radio :value="1">男</el-radio>
            <el-radio :value="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="2">锁定</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.roleIds" multiple placeholder="选择角色（决定可访问的 API）" style="width: 100%">
            <el-option v-for="r in roles" :key="r.id" :label="r.name" :value="r.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="onSave">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="pwdVisible" title="重置密码" width="420px">
      <el-form label-width="90px">
        <el-form-item label="账号">
          <el-input :model-value="pwdForm.uid" disabled />
        </el-form-item>
        <el-form-item label="新密码" required>
          <el-input v-model="pwdForm.password" type="password" show-password placeholder="输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="onResetPwd">确认重置</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, addUser, updateUser, deleteUser, resetPassword, getUserByUid, getRoleList } from '../api'

const users = ref([])
const roles = ref([])
const total = ref(0)
const pageIndex = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const pwdVisible = ref(false)
const editingUid = ref('')
const form = ref({ uid: '', username: '', password: '', realName: '', phone: '', email: '', sex: 1, status: 1, roleIds: [] })
const pwdForm = ref({ uid: '', password: '' })

async function load(page = pageIndex.value) {
  pageIndex.value = page
  loading.value = true
  try {
    const data = await getUserList(String(page), String(pageSize.value))
    if (data && data.list) {
      users.value = data.list
      total.value = Number(data.total || 0)
    } else {
      users.value = []
      total.value = 0
    }
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}

async function loadRoles() {
  try {
    const data = await getRoleList('1', '100')
    roles.value = (data && data.list) || []
  } catch (e) {
    roles.value = []
  }
}

async function openDialog(row) {
  editingUid.value = row ? row.uid : ''
  if (row) {
    form.value = {
      uid: row.uid,
      username: row.username || '',
      password: '',
      realName: row.realName || '',
      phone: row.phone || '',
      email: row.email || '',
      sex: Number(row.sex) || 1,
      status: Number(row.status) === 1 ? 1 : 2,
      roleIds: []
    }
    dialogVisible.value = true
    // 编辑时回显角色
    try {
      const d = await getUserByUid(row.uid)
      if (d && d.roleIds) form.value.roleIds = d.roleIds.map(Number)
    } catch (e) { /* 忽略回显失败 */ }
  } else {
    form.value = { uid: '', username: '', password: '', realName: '', phone: '', email: '', sex: 1, status: 1, roleIds: [] }
    dialogVisible.value = true
  }
}

async function onSave() {
  if (!form.value.uid || (!editingUid.value && !form.value.password)) {
    ElMessage.warning('请填写账号和密码')
    return
  }
  saving.value = true
  try {
    const data = {
      uid: form.value.uid,
      username: form.value.username || '',
      password: form.value.password || '',
      realName: form.value.realName || '',
      phone: form.value.phone || '',
      email: form.value.email || '',
      sex: String(form.value.sex),
      status: String(form.value.status),
      roleIds: form.value.roleIds.join(',')
    }
    if (editingUid.value) {
      await updateUser(data)
      ElMessage.success('更新成功')
    } else {
      await addUser(data)
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

function openResetPwd(row) {
  pwdForm.value = { uid: row.uid, password: '' }
  pwdVisible.value = true
}

async function onResetPwd() {
  if (!pwdForm.value.password) {
    ElMessage.warning('请输入新密码')
    return
  }
  saving.value = true
  try {
    await resetPassword({ uid: pwdForm.value.uid, password: pwdForm.value.password })
    ElMessage.success('密码已重置')
    pwdVisible.value = false
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    saving.value = false
  }
}

async function onDelete(row) {
  try {
    await ElMessageBox.confirm(`确认删除用户「${row.uid}」？删除后不可恢复。`, '提示', { type: 'warning' })
  } catch {
    return
  }
  try {
    await deleteUser(row.uid)
    ElMessage.success('删除成功')
    load()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

onMounted(() => {
  load()
  loadRoles()
})
</script>

<style scoped>
.toolbar { margin-bottom: 12px; }
.pager { margin-top: 12px; justify-content: flex-end; }
</style>
