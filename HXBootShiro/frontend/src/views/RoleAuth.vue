<template>
  <el-card>
    <div class="header-row">
      <el-button @click="$router.push('/role')"><el-icon><ArrowLeft /></el-icon>返回</el-button>
      <span class="title">角色授权 — {{ roleName || `角色#${roleId}` }}</span>
      <el-badge v-if="pendingCount > 0" :value="pendingCount" type="warning" class="pending-badge">
        <el-tag type="warning" effect="plain">待保存变更</el-tag>
      </el-badge>
    </div>

    <el-tabs v-model="tab">
      <!-- API 资源授权 -->
      <el-tab-pane label="API 资源授权" name="api">
        <el-row :gutter="16" align="middle">
          <el-col :span="11">
            <el-card shadow="never" class="sub-card">
              <template #header>
                <span>未授权 API</span>
                <span class="tip">（点选后点 → 授权，或 ← 还原）</span>
              </template>
              <el-table :data="notAuthApis" height="420" border size="small"
                :row-class-name="apiRowClass" @row-click="toggleSelect($event, 'not', 'api')">
                <el-table-column type="selection" width="40" :selectable="() => false"
                  :reserve-selection="false" />
                <el-table-column prop="id" label="ID" width="60" />
                <el-table-column prop="name" label="接口名称" />
                <el-table-column prop="uri" label="URI" show-overflow-tooltip />
                <el-table-column prop="method" label="方法" width="70" />
              </el-table>
              <el-pagination small background layout="prev, pager, next" :total="notAuthTotal"
                :page-size="10" :current-page="notAuthPage" @current-change="p => loadApis(p, 'not')" />
            </el-card>
          </el-col>

          <el-col :span="2" class="transfer-col">
            <el-button type="primary" circle :disabled="!selectedNotApis.size" @click="moveToAuth">
              <el-icon><ArrowRight /></el-icon>
            </el-button>
            <el-button circle :disabled="!selectedAuthApis.size" @click="moveToNot">
              <el-icon><ArrowLeft /></el-icon>
            </el-button>
          </el-col>

          <el-col :span="11">
            <el-card shadow="never" class="sub-card">
              <template #header>
                <span>已授权 API</span>
                <span class="tip">（绿色=本次新增，点选后点 ← 还原）</span>
              </template>
              <el-table :data="authApis" height="420" border size="small"
                :row-class-name="apiRowClass" @row-click="toggleSelect($event, 'auth', 'api')">
                <el-table-column type="selection" width="40" :selectable="() => false"
                  :reserve-selection="false" />
                <el-table-column prop="id" label="ID" width="60" />
                <el-table-column prop="name" label="接口名称" />
                <el-table-column prop="uri" label="URI" show-overflow-tooltip />
                <el-table-column prop="method" label="方法" width="70" />
              </el-table>
              <el-pagination small background layout="prev, pager, next" :total="authTotal"
                :page-size="10" :current-page="authPage" @current-change="p => loadApis(p, 'auth')" />
            </el-card>
          </el-col>
        </el-row>
        <div class="save-row">
          <el-button type="success" :loading="saving" :disabled="pendingCount === 0" @click="saveChanges">
            <el-icon><Check /></el-icon>保存授权变更
          </el-button>
        </div>
      </el-tab-pane>

      <!-- 用户关联 -->
      <el-tab-pane label="用户关联" name="user">
        <el-row :gutter="16" align="middle">
          <el-col :span="11">
            <el-card shadow="never" class="sub-card">
              <template #header>
                <span>未关联用户</span>
                <span class="tip">（点选后点 → 关联，或 ← 还原）</span>
              </template>
              <el-table :data="notAuthUsers" height="420" border size="small"
                :row-class-name="userRowClass" @row-click="toggleSelect($event, 'not', 'user')">
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
                :row-class-name="userRowClass" @row-click="toggleSelect($event, 'auth', 'user')">
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
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  initRoleByroleId, getRestApiByRoleId, getRestApiExtendByRoleId,
  authorityRoleResource, deleteAuthorityRoleResource,
  getUserListByRoleId, getUserListExtendByRoleId,
  authorityRoleUser, deleteAuthorityRoleUser
} from '../api'

const route = useRoute()
const roleId = route.params.id
const roleName = ref('')
const tab = ref('api')
const saving = ref(false)

// ============ API 授权（穿梭框：选中→箭头移动→保存） ============
// raw：后端当前页数据；pendingGrant：本次新增（右侧，绿底）；pendingRevoke：本次还原（左侧，黄底）
const rawNotApis = ref([])
const rawAuthApis = ref([])
const pendingGrantApis = ref([])
const pendingRevokeApis = ref([])
const notAuthTotal = ref(0)
const authTotal = ref(0)
const notAuthPage = ref(1)
const authPage = ref(1)
const selectedNotApis = ref(new Set())
const selectedAuthApis = ref(new Set())

// 展示列表 = 后端数据 + 待办调整
const notAuthApis = computed(() => {
  const grantIds = new Set(pendingGrantApis.value.map(x => x.id))
  const revokeIds = new Set(pendingRevokeApis.value.map(x => x.id))
  const fromRaw = rawNotApis.value.filter(x => !grantIds.has(x.id))
  const fromRevoke = pendingRevokeApis.value.filter(x => !grantIds.has(x.id))
  return [...fromRaw, ...fromRevoke]
})
const authApis = computed(() => {
  const revokeIds = new Set(pendingRevokeApis.value.map(x => x.id))
  const grantIds = new Set(pendingGrantApis.value.map(x => x.id))
  const fromRaw = rawAuthApis.value.filter(x => !revokeIds.has(x.id))
  const fromGrant = pendingGrantApis.value.filter(x => !revokeIds.has(x.id))
  return [...fromRaw, ...fromGrant]
})

function apiRowClass({ row }) {
  const classes = []
  if (pendingGrantApis.value.some(x => x.id === row.id)) classes.push('row-pending-grant')
  if (pendingRevokeApis.value.some(x => x.id === row.id)) classes.push('row-pending-revoke')
  if (selectedNotApis.value.has(row.id) || selectedAuthApis.value.has(row.id)) classes.push('row-selected')
  return classes.join(' ')
}

function toggleSelect(row, side, kind) {
  if (kind === 'api') {
    const set = side === 'not' ? selectedNotApis.value : selectedAuthApis.value
    if (set.has(row.id)) set.delete(row.id)
    else set.add(row.id)
  } else {
    const set = side === 'not' ? selectedNotUsers.value : selectedAuthUsers.value
    if (set.has(row.uid)) set.delete(row.uid)
    else set.add(row.uid)
  }
}

// 未授权 → 已授权（新增）
function moveToAuth() {
  const ids = [...selectedNotApis.value]
  ids.forEach(id => {
    const item = rawNotApis.value.find(x => x.id === id)
    if (item) pendingGrantApis.value.push(item)
  })
  // 若此前刚被还原过（在 pendingRevoke 中），还原取消
  pendingRevokeApis.value = pendingRevokeApis.value.filter(x => !ids.includes(x.id))
  selectedNotApis.value = new Set()
  ElMessage.success(`已选 ${ids.length} 个接口，点击保存生效`)
}

// 已授权 → 未授权（还原）
function moveToNot() {
  const ids = [...selectedAuthApis.value]
  ids.forEach(id => {
    // 在本次新增列表中 → 直接移除新增；否则记为还原
    const grantIdx = pendingGrantApis.value.findIndex(x => x.id === id)
    if (grantIdx >= 0) {
      pendingGrantApis.value.splice(grantIdx, 1)
    } else {
      const item = rawAuthApis.value.find(x => x.id === id)
      if (item) pendingRevokeApis.value.push(item)
    }
  })
  selectedAuthApis.value = new Set()
  ElMessage.success(`已选 ${ids.length} 个接口，点击保存生效`)
}

async function loadApis(page, side) {
  if (side === 'not') {
    notAuthPage.value = page
    const d = await getRestApiByRoleId(roleId, String(page), '10')
    if (d && d.list) { rawNotApis.value = d.list; notAuthTotal.value = Number(d.total || 0) }
    selectedNotApis.value = new Set()
  } else {
    authPage.value = page
    const d = await getRestApiExtendByRoleId(roleId, String(page), '10')
    if (d && d.list) { rawAuthApis.value = d.list; authTotal.value = Number(d.total || 0) }
    selectedAuthApis.value = new Set()
  }
}

// ============ 用户关联（同上） ============
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

function userRowClass({ row }) {
  const classes = []
  if (pendingGrantUsers.value.some(x => x.uid === row.uid)) classes.push('row-pending-grant')
  if (pendingRevokeUsers.value.some(x => x.uid === row.uid)) classes.push('row-pending-revoke')
  if (selectedNotUsers.value.has(row.uid) || selectedAuthUsers.value.has(row.uid)) classes.push('row-selected')
  return classes.join(' ')
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
  if (side === 'not') {
    notUserPage.value = page
    const d = await getUserListExtendByRoleId(roleId, String(page), '10')
    if (d && d.list) { rawNotUsers.value = d.list; notUserTotal.value = Number(d.total || 0) }
    selectedNotUsers.value = new Set()
  } else {
    authUserPage.value = page
    const d = await getUserListByRoleId(roleId, String(page), '10')
    if (d && d.list) { rawAuthUsers.value = d.list; authUserTotal.value = Number(d.total || 0) }
    selectedAuthUsers.value = new Set()
  }
}

// ============ 保存 ============
const pendingCount = computed(() =>
  pendingGrantApis.value.length + pendingRevokeApis.value.length +
  pendingGrantUsers.value.length + pendingRevokeUsers.value.length)

async function saveChanges() {
  saving.value = true
  try {
    // API：新增授权
    for (const item of pendingGrantApis.value) {
      await authorityRoleResource({ roleId, resourceId: item.id })
    }
    // API：还原
    for (const item of pendingRevokeApis.value) {
      await deleteAuthorityRoleResource({ roleId, resourceId: item.id })
    }
    // 用户：新增关联
    for (const item of pendingGrantUsers.value) {
      await authorityRoleUser({ roleId, resourceId: item.uid })
    }
    // 用户：取消关联
    for (const item of pendingRevokeUsers.value) {
      await deleteAuthorityRoleUser({ roleId, resourceId: item.uid })
    }
    const total = pendingCount.value
    pendingGrantApis.value = []
    pendingRevokeApis.value = []
    pendingGrantUsers.value = []
    pendingRevokeUsers.value = []
    ElMessage.success(`保存成功，共生效 ${total} 项变更`)
    // 刷新两侧数据
    loadApis(notAuthPage.value, 'not')
    loadApis(authPage.value, 'auth')
    loadUsers(notUserPage.value, 'not')
    loadUsers(authUserPage.value, 'auth')
  } catch (e) {
    ElMessage.error(`保存失败：${e.message}`)
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  try {
    const info = await initRoleByroleId(roleId)
    if (info && info.name) roleName.value = info.name
  } catch {}
  loadApis(1, 'not')
  loadApis(1, 'auth')
  loadUsers(1, 'not')
  loadUsers(1, 'auth')
})
</script>

<style scoped>
.header-row { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; }
.title { font-size: 16px; font-weight: 600; }
.pending-badge { margin-left: auto; }
.sub-card { margin-bottom: 8px; }
.tip { font-size: 12px; color: #909399; margin-left: 8px; }
.transfer-col { display: flex; flex-direction: column; align-items: center; gap: 12px; }
.save-row { display: flex; justify-content: center; margin-top: 12px; }
</style>

<style>
/* 全局样式：表格行背景色（需要穿透 scoped） */
.row-pending-grant td { background-color: #f0f9eb !important; }
.row-pending-revoke td { background-color: #fdf6ec !important; }
.row-selected td { background-color: #ecf5ff !important; }
</style>
