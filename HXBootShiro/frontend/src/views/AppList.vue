<template>
  <el-card>
    <div class="toolbar">
      <el-button type="primary" @click="openDialog()"><el-icon><Plus /></el-icon>新增应用</el-button>
    </div>
    <el-table :data="apps" v-loading="loading" border stripe>
      <el-table-column prop="appId" label="应用标识" width="180" />
      <el-table-column prop="appName" label="应用名称" width="140" />
      <el-table-column prop="description" label="描述" min-width="180">
        <template #default="{ row }">{{ row.description || '-' }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="Number(row.status) === 1 ? 'success' : 'info'">
            {{ Number(row.status) === 1 ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="License 状态" width="230">
        <template #default="{ row }">
          <template v-if="row.license">
            <div class="license-line">
              <el-tag v-if="Number(row.licenseExpireAt) === 0" type="success">永久有效</el-tag>
              <el-tag v-else-if="Number(row.licenseExpireAt) < Date.now()" type="danger">已过期</el-tag>
              <el-tag v-else-if="daysLeft(row.licenseExpireAt) <= 7" type="warning">剩 {{ daysLeft(row.licenseExpireAt) }} 天</el-tag>
              <el-tag v-else type="primary">剩 {{ daysLeft(row.licenseExpireAt) }} 天</el-tag>
              <el-button v-if="row.license" link type="primary" size="small" @click="copyLicense(row)">复制</el-button>
            </div>
            <div class="license-mask" :title="'点击复制完整 License'">{{ maskLicense(row.license) }}</div>
            <div class="license-meta" :title="`生成时间：${formatTime(row.licenseGeneratedAt)}`">
              生成于 {{ formatTime(row.licenseGeneratedAt) }}
            </div>
          </template>
          <el-tag v-else type="info">未生成</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button link type="success" @click="openLicense(row)">生成License</el-button>
          <el-button link type="danger" @click="onDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      class="pager" background layout="total, prev, pager, next"
      :total="total" :page-size="pageSize" :current-page="pageIndex"
      @current-change="load" />

    <!-- 新增/编辑应用 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑应用' : '新增应用'" width="520px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="应用名称" required>
          <el-input v-model="form.appName" placeholder="如：订单服务" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="应用用途说明" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.roleIds" multiple placeholder="选择角色（决定该应用可访问的 API）" style="width: 100%">
            <el-option v-for="r in roles" :key="r.id" :label="r.name" :value="r.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="onSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 生成 JWT License -->
    <el-dialog v-model="licenseVisible" title="生成 API 访问 License (JWT)" width="640px">
      <el-form label-width="100px">
        <el-form-item label="应用标识">
          <el-input :model-value="licenseForm.appId" disabled />
        </el-form-item>
        <el-form-item label="有效期">
          <el-input-number v-model="licenseForm.expireDays" :min="0" :max="3650" />
          <span class="unit">{{ licenseForm.expireDays === 0 ? '（0 = 永久有效）' : '天' }}</span>
        </el-form-item>
        <el-form-item v-if="licenseForm.expireDays === 0" label="安全提醒">
          <el-alert type="warning" :closable="false" show-icon
            title="永久 License 存在安全隐患，建议每 90 天重新生成轮换一次（重新生成后旧 License 自动失效）" />
        </el-form-item>
        <el-form-item label="License">
          <el-input v-model="licenseForm.jwt" type="textarea" :rows="6" readonly placeholder="点击下方按钮生成" />
        </el-form-item>
        <el-form-item label="过期时间" v-if="licenseForm.expireAt">
          <span>{{ licenseForm.permanent ? '永久有效' : formatTime(licenseForm.expireAt) }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="licenseVisible = false">关闭</el-button>
        <el-button type="success" :loading="generating" @click="onGenerate">生成</el-button>
        <el-button type="primary" :disabled="!licenseForm.jwt" @click="onCopy">复制 License</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAppList, addApp, updateApp, deleteApp, getAppByAppId, generateLicense, getRoleList } from '../api'

const apps = ref([])
const roles = ref([])
const total = ref(0)
const pageIndex = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const saving = ref(false)
const generating = ref(false)
const dialogVisible = ref(false)
const licenseVisible = ref(false)
const form = ref({ id: null, appName: '', description: '', status: 1, roleIds: [] })
const licenseForm = ref({ appId: '', expireDays: 0, jwt: '', expireAt: 0, roles: [], permanent: false })

async function load(page = pageIndex.value) {
  pageIndex.value = page
  loading.value = true
  try {
    const data = await getAppList(String(page), String(pageSize.value))
    if (data && data.list) {
      apps.value = data.list
      total.value = Number(data.total || 0)
    } else {
      apps.value = []
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
  if (row) {
    form.value = { id: row.id, appName: row.appName, description: row.description || '', status: Number(row.status) === 1 ? 1 : 0, roleIds: [] }
    dialogVisible.value = true
    try {
      const d = await getAppByAppId(row.appId)
      if (d && d.roleIds) form.value.roleIds = d.roleIds.map(Number)
    } catch (e) { /* 忽略回显失败 */ }
  } else {
    form.value = { id: null, appName: '', description: '', status: 1, roleIds: [] }
    dialogVisible.value = true
  }
}

async function onSave() {
  if (!form.value.appName) {
    ElMessage.warning('请填写应用名称')
    return
  }
  saving.value = true
  try {
    const data = {
      appName: form.value.appName,
      description: form.value.description || '',
      status: String(form.value.status),
      roleIds: form.value.roleIds.join(',')
    }
    if (form.value.id) {
      await updateApp({ ...data, id: String(form.value.id) })
      ElMessage.success('更新成功')
    } else {
      await addApp(data)
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
  try {
    await ElMessageBox.confirm(`确认删除应用「${row.appName}」？其 License 将全部失效。`, '提示', { type: 'warning' })
  } catch {
    return
  }
  try {
    await deleteApp(String(row.id))
    ElMessage.success('删除成功')
    load()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

function openLicense(row) {
  licenseForm.value = { appId: row.appId, expireDays: 0, jwt: '', expireAt: 0, roles: [], permanent: false }
  licenseVisible.value = true
}

async function onGenerate() {
  generating.value = true
  try {
    const d = await generateLicense(licenseForm.value.appId, { expireDays: String(licenseForm.value.expireDays) })
    if (d && d.jwt) {
      licenseForm.value.jwt = d.jwt
      licenseForm.value.expireAt = d.expireAt
      licenseForm.value.roles = d.roles || []
      licenseForm.value.permanent = !!d.permanent
      ElMessage.success(d.permanent
        ? '永久 License 已生成并保存至本地缓存，调用网关 API 时携带 userId: appId + Authorization: License'
        : `License 已生成并保存至本地缓存，有效期 ${d.expireSeconds / 86400} 天`)
      load()
    } else {
      ElMessage.error('生成失败')
    }
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    generating.value = false
  }
}

function daysLeft(expireAt) {
  return Math.ceil((Number(expireAt) - Date.now()) / 86400000)
}

// 脱敏显示：JWT 只展示头尾，完整值仅复制时使用
function maskLicense(jwt) {
  if (!jwt) return ''
  if (jwt.length <= 30) return jwt
  return `${jwt.slice(0, 20)}...${jwt.slice(-12)}`
}

async function copyLicense(row) {
  const text = row.license
  if (!text) return
  try {
    await navigator.clipboard.writeText(text)
    ElMessage.success('License 已复制到剪贴板')
  } catch (e) {
    // 剪贴板 API 不可用时回退
    const ta = document.createElement('textarea')
    ta.value = text
    document.body.appendChild(ta)
    ta.select()
    document.execCommand('copy')
    document.body.removeChild(ta)
    ElMessage.success('License 已复制到剪贴板')
  }
}

async function onCopy() {
  try {
    await navigator.clipboard.writeText(licenseForm.value.jwt)
    ElMessage.success('已复制到剪贴板')
  } catch (e) {
    // 剪贴板 API 不可用时回退
    const ta = document.createElement('textarea')
    ta.value = licenseForm.value.jwt
    document.body.appendChild(ta)
    ta.select()
    document.execCommand('copy')
    document.body.removeChild(ta)
    ElMessage.success('已复制到剪贴板')
  }
}

function formatTime(ts) {
  const d = new Date(Number(ts))
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

onMounted(() => {
  load()
  loadRoles()
})
</script>

<style scoped>
.toolbar { margin-bottom: 12px; }
.pager { margin-top: 12px; justify-content: flex-end; }
.unit { margin-left: 8px; color: #999; }
.license-line { display: flex; align-items: center; gap: 6px; }
.license-mask {
  font-size: 12px;
  color: #606266;
  font-family: monospace;
  margin-top: 2px;
  cursor: pointer;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.license-meta { font-size: 12px; color: #909399; margin-top: 2px; white-space: nowrap; }
</style>
