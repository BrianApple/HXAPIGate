<template>
  <el-card>
    <div class="header-row">
      <span class="title">用户角色关联</span>
      <el-select v-model="roleId" placeholder="选择角色" style="width: 240px" @change="onRoleChange">
        <el-option v-for="r in roles" :key="r.id" :label="`${r.name}（${r.code}）`" :value="r.id" />
      </el-select>
      <span class="tip">将用户关联到所选角色，角色即拥有该用户</span>
      <el-badge v-if="pendingCount > 0" :value="pendingCount" type="warning" class="pending-badge">
        <el-tag type="warning" effect="plain">待保存变更</el-tag>
      </el-badge>
    </div>

    <el-row :gutter="16" align="middle">
      <el-col :span="11">
        <el-card shadow="never" class="sub-card">
          <template #header>
            <span>未关联用户</span>
            <span class="tip">（点选后点 → 关联，或 ← 还原）</span>
          </template>
          <el-table :data="notAuthUsers" height="420" border size="small"
            :row-class-name="userRowClass" @row-click="toggleSelect($event, 'not')">
            <el-table-column type="selection" width="40" :selectable="() => false"
              :reserve-selection="false" />
            <el-table-column prop="uid" label="账号" width="120" />
            <el-table-column prop="username" label="用户名" />
          </el-table>
          <el-pagination small background layout="prev, pager, next" :total="notUserTotal"
            :page-size="10" :current-page="notUserPage" @current-change="p => loadUsers(p, 'not')" />
        </el-card>
      </el-col>

      <el-col :span="2" class="transfer-col">
        <el-button type="primary" circle :disabled="!selectedNotUsers.size" @click="moveToAuthUsers">
          <el-icon><ArrowRight /></el-icon>
        </el-button>
        <el-button circle :disabled="!selectedAuthUsers.size" @click="moveToNotUsers">
          <el-icon><ArrowLeft /></el-icon>
        </el-button>
      </el-col>

      <el-col :span="11">
        <el-card shadow="never" class="sub-card">
          <template #header>
            <span>已关联用户</span>
            <span class="tip">（绿色=本次新增，点选后点 ← 还原）</span>
          </template>
          <el-table :data="authUsers" height="420" border size="small"
            :row-class-name="userRowClass" @row-click="toggleSelect($event, 'auth')">
            <el-table-column type="selection" width="40" :selectable="() => false"
              :reserve-selection="false" />
            <el-table-column prop="uid" label="账号" width="120" />
            <el-table-column prop="username" label="用户名" />
          </el-table>
          <el-pagination small background layout="prev, pager, next" :total="authUserTotal"
            :page-size="10" :current-page="authUserPage" @current-change="p => loadUsers(p, 'auth')" />
        </el-card>
      </el-col>
    </el-row>
    <div class="save-row">
      <el-button type="success" :loading="saving" :disabled="pendingCount === 0" @click="saveChanges">
        <el-icon><Check /></el-icon>保存关联变更
      </el-button>
    </div>
  </el-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getRoleList, getUserListByRoleId, getUserListExtendByRoleId,
  authorityRoleUser, deleteAuthorityRoleUser
} from '../api'

const roles = ref([])
const roleId = ref(null)

// ============ 用户关联（穿梭框：选中→箭头移动→保存） ============
const rawNotUsers = ref([])
const rawAuthUsers = ref([])
const pendingGrantUsers = ref([])
const pendingRevokeUsers = ref([])
const notUserTotal = ref(0)
const authUserTotal = ref(0)
const notUserPage = ref(1)
const authUserPage = ref(1)
const selectedNotUsers = ref(new Set())
const selectedAuthUsers = ref(new Set())
const saving = ref(false)

const notAuthUsers = computed(() => {
  const grantIds = new Set(pendingGrantUsers.value.map(x => x.uid))
  const revokeIds = new Set(pendingRevokeUsers.value.map(x => x.uid))
  const fromRaw = rawNotUsers.value.filter(x => !grantIds.has(x.uid))
  const fromRevoke = pendingRevokeUsers.value.filter(x => !grantIds.has(x.uid))
  return [...fromRaw, ...fromRevoke]
})
const authUsers = computed(() => {
  const revokeIds = new Set(pendingRevokeUsers.value.map(x => x.uid))
  const grantIds = new Set(pendingGrantUsers.value.map(x => x.uid))
  const fromRaw = rawAuthUsers.value.filter(x => !revokeIds.has(x.uid))
  const fromGrant = pendingGrantUsers.value.filter(x => !revokeIds.has(x.uid))
  return [...fromRaw, ...fromGrant]
})

const pendingCount = computed(() =>
  pendingGrantUsers.value.length + pendingRevokeUsers.value.length)

function userRowClass({ row }) {
  const classes = []
  if (pendingGrantUsers.value.some(x => x.uid === row.uid)) classes.push('row-pending-grant')
  if (pendingRevokeUsers.value.some(x => x.uid === row.uid)) classes.push('row-pending-revoke')
  if (selectedNotUsers.value.has(row.uid) || selectedAuthUsers.value.has(row.uid)) classes.push('row-selected')
  return classes.join(' ')
}

function toggleSelect(row, side) {
  const set = side === 'not' ? selectedNotUsers.value : selectedAuthUsers.value
  if (set.has(row.uid)) set.delete(row.uid)
  else set.add(row.uid)
}

function moveToAuthUsers() {
  const ids = [...selectedNotUsers.value]
  ids.forEach(uid => {
    const item = rawNotUsers.value.find(x => x.uid === uid)
    if (item) pendingGrantUsers.value.push(item)
  })
  pendingRevokeUsers.value = pendingRevokeUsers.value.filter(x => !ids.includes(x.uid))
  selectedNotUsers.value = new Set()
  ElMessage.success(`已选 ${ids.length} 个用户，点击保存生效`)
}

function moveToNotUsers() {
  const ids = [...selectedAuthUsers.value]
  ids.forEach(uid => {
    const grantIdx = pendingGrantUsers.value.findIndex(x => x.uid === uid)
    if (grantIdx >= 0) {
      pendingGrantUsers.value.splice(grantIdx, 1)
    } else {
      const item = rawAuthUsers.value.find(x => x.uid === uid)
      if (item) pendingRevokeUsers.value.push(item)
    }
  })
  selectedAuthUsers.value = new Set()
  ElMessage.success(`已选 ${ids.length} 个用户，点击保存生效`)
}

async function loadUsers(page, side) {
  if (!roleId.value) return
  if (side === 'not') {
    notUserPage.value = page
    const d = await getUserListExtendByRoleId(roleId.value, String(page), '10')
    if (d && d.list) { rawNotUsers.value = d.list; notUserTotal.value = Number(d.total || 0) }
    selectedNotUsers.value = new Set()
  } else {
    authUserPage.value = page
    const d = await getUserListByRoleId(roleId.value, String(page), '10')
    if (d && d.list) { rawAuthUsers.value = d.list; authUserTotal.value = Number(d.total || 0) }
    selectedAuthUsers.value = new Set()
  }
}

// 切换角色：清空待办并重新加载两侧
function onRoleChange() {
  pendingGrantUsers.value = []
  pendingRevokeUsers.value = []
  notUserPage.value = 1
  authUserPage.value = 1
  loadUsers(1, 'not')
  loadUsers(1, 'auth')
}

async function saveChanges() {
  saving.value = true
  try {
    for (const item of pendingGrantUsers.value) {
      await authorityRoleUser({ roleId: roleId.value, resourceId: item.uid })
    }
    for (const item of pendingRevokeUsers.value) {
      await deleteAuthorityRoleUser({ roleId: roleId.value, resourceId: item.uid })
    }
    const total = pendingCount.value
    pendingGrantUsers.value = []
    pendingRevokeUsers.value = []
    ElMessage.success(`保存成功，共生效 ${total} 项变更`)
    // 刷新两侧数据
    loadUsers(notUserPage.value, 'not')
    loadUsers(authUserPage.value, 'auth')
  } catch (e) {
    ElMessage.error(`保存失败：${e.message}`)
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  const d = await getRoleList(1, 1000)
  if (d && d.list && d.list.length) {
    roles.value = d.list
    roleId.value = d.list[0].id
    loadUsers(1, 'not')
    loadUsers(1, 'auth')
  }
})
</script>

<style scoped>
.header-row { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; }
.title { font-size: 16px; font-weight: 600; }
.pending-badge { margin-left: auto; }
.sub-card { margin-bottom: 8px; }
.tip { font-size: 12px; color: #909399; margin-left: 8px; }
.transfer-col {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 12px;
  align-self: stretch;
}
.save-row { display: flex; justify-content: center; margin-top: 12px; }
</style>

<style>
/* 全局样式：表格行背景色（需要穿透 scoped） */
.row-pending-grant td { background-color: #f0f9eb !important; }
.row-pending-revoke td { background-color: #fdf6ec !important; }
.row-selected td { background-color: #ecf5ff !important; }
</style>
