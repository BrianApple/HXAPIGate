<template>
  <el-card>
    <div class="header-row">
      <span class="title">角色资源授权</span>
      <el-select v-model="roleId" placeholder="选择角色" style="width: 240px" @change="onRoleChange">
        <el-option v-for="r in roles" :key="r.id" :label="`${r.name}（${r.code}）`" :value="r.id" />
      </el-select>
      <el-badge v-if="pendingCount > 0" :value="pendingCount" type="warning" class="pending-badge">
        <el-tag type="warning" effect="plain">待保存变更</el-tag>
      </el-badge>
    </div>

    <el-row :gutter="16" align="middle">
      <el-col :span="11">
        <el-card shadow="never" class="sub-card">
          <template #header>
            <span>未授权 API</span>
            <span class="tip">（点选后点 → 授权，或 ← 还原）</span>
          </template>
          <el-table :data="notAuthApis" height="420" border size="small"
            :row-class-name="apiRowClass" @row-click="toggleSelect($event, 'not')">
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
            :row-class-name="apiRowClass" @row-click="toggleSelect($event, 'auth')">
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
  </el-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getRoleList, getRestApiByRoleId, getRestApiExtendByRoleId,
  authorityRoleResource, deleteAuthorityRoleResource
} from '../api'

const roles = ref([])
const roleId = ref(null)

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
const saving = ref(false)

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

const pendingCount = computed(() =>
  pendingGrantApis.value.length + pendingRevokeApis.value.length)

function apiRowClass({ row }) {
  const classes = []
  if (pendingGrantApis.value.some(x => x.id === row.id)) classes.push('row-pending-grant')
  if (pendingRevokeApis.value.some(x => x.id === row.id)) classes.push('row-pending-revoke')
  if (selectedNotApis.value.has(row.id) || selectedAuthApis.value.has(row.id)) classes.push('row-selected')
  return classes.join(' ')
}

function toggleSelect(row, side) {
  const set = side === 'not' ? selectedNotApis.value : selectedAuthApis.value
  if (set.has(row.id)) set.delete(row.id)
  else set.add(row.id)
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
  if (!roleId.value) return
  if (side === 'not') {
    notAuthPage.value = page
    const d = await getRestApiByRoleId(roleId.value, String(page), '10')
    if (d && d.list) { rawNotApis.value = d.list; notAuthTotal.value = Number(d.total || 0) }
    selectedNotApis.value = new Set()
  } else {
    authPage.value = page
    const d = await getRestApiExtendByRoleId(roleId.value, String(page), '10')
    if (d && d.list) { rawAuthApis.value = d.list; authTotal.value = Number(d.total || 0) }
    selectedAuthApis.value = new Set()
  }
}

// 切换角色：清空待办并重新加载两侧
function onRoleChange() {
  pendingGrantApis.value = []
  pendingRevokeApis.value = []
  notAuthPage.value = 1
  authPage.value = 1
  loadApis(1, 'not')
  loadApis(1, 'auth')
}

async function saveChanges() {
  saving.value = true
  try {
    for (const item of pendingGrantApis.value) {
      await authorityRoleResource({ roleId: roleId.value, resourceId: item.id })
    }
    for (const item of pendingRevokeApis.value) {
      await deleteAuthorityRoleResource({ roleId: roleId.value, resourceId: item.id })
    }
    const total = pendingCount.value
    pendingGrantApis.value = []
    pendingRevokeApis.value = []
    ElMessage.success(`保存成功，共生效 ${total} 项变更`)
    // 刷新两侧数据
    loadApis(notAuthPage.value, 'not')
    loadApis(authPage.value, 'auth')
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
    loadApis(1, 'not')
    loadApis(1, 'auth')
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
