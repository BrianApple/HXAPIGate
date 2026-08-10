<template>
  <el-card>
    <div class="header-row">
      <el-button @click="$router.push('/role')"><el-icon><ArrowLeft /></el-icon>返回</el-button>
      <span class="title">角色授权 — {{ roleName || `角色#${roleId}` }}</span>
    </div>

    <el-tabs v-model="tab">
      <!-- API 资源授权 -->
      <el-tab-pane label="API 资源授权" name="api">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-card shadow="never" class="sub-card">
              <template #header>未授权 API（点击授权 →）</template>
              <el-table :data="notAuthApis" height="420" border size="small" @row-click="grantApi">
                <el-table-column prop="id" label="ID" width="60" />
                <el-table-column prop="name" label="接口名称" />
                <el-table-column prop="uri" label="URI" show-overflow-tooltip />
                <el-table-column prop="method" label="方法" width="70" />
              </el-table>
              <el-pagination small background layout="prev, pager, next" :total="notAuthTotal"
                :page-size="10" :current-page="notAuthPage" @current-change="p => loadApis(p, 'not')" />
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="never" class="sub-card">
              <template #header>已授权 API（点击取消授权）</template>
              <el-table :data="authApis" height="420" border size="small" @row-click="revokeApi">
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
      </el-tab-pane>

      <!-- 用户关联 -->
      <el-tab-pane label="用户关联" name="user">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-card shadow="never" class="sub-card">
              <template #header>未关联用户（点击关联 →）</template>
              <el-table :data="notAuthUsers" height="420" border size="small" @row-click="grantUser">
                <el-table-column prop="uid" label="账号" width="120" />
                <el-table-column prop="username" label="用户名" />
              </el-table>
              <el-pagination small background layout="prev, pager, next" :total="notUserTotal"
                :page-size="10" :current-page="notUserPage" @current-change="p => loadUsers(p, 'not')" />
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="never" class="sub-card">
              <template #header>已关联用户（点击取消关联）</template>
              <el-table :data="authUsers" height="420" border size="small" @row-click="revokeUser">
                <el-table-column prop="uid" label="账号" width="120" />
                <el-table-column prop="username" label="用户名" />
              </el-table>
              <el-pagination small background layout="prev, pager, next" :total="authUserTotal"
                :page-size="10" :current-page="authUserPage" @current-change="p => loadUsers(p, 'auth')" />
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
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

// API 授权
const notAuthApis = ref([])
const authApis = ref([])
const notAuthTotal = ref(0)
const authTotal = ref(0)
const notAuthPage = ref(1)
const authPage = ref(1)

// 用户关联
const notAuthUsers = ref([])
const authUsers = ref([])
const notUserTotal = ref(0)
const authUserTotal = ref(0)
const notUserPage = ref(1)
const authUserPage = ref(1)

async function loadApis(page, side) {
  if (side === 'not') {
    notAuthPage.value = page
    const d = await getRestApiByRoleId(roleId, String(page), '10')
    if (d && d.list) { notAuthApis.value = d.list; notAuthTotal.value = Number(d.total || 0) }
  } else {
    authPage.value = page
    const d = await getRestApiExtendByRoleId(roleId, String(page), '10')
    if (d && d.list) { authApis.value = d.list; authTotal.value = Number(d.total || 0) }
  }
}

async function loadUsers(page, side) {
  if (side === 'not') {
    notUserPage.value = page
    const d = await getUserListExtendByRoleId(roleId, String(page), '10')
    if (d && d.list) { notAuthUsers.value = d.list; notUserTotal.value = Number(d.total || 0) }
  } else {
    authUserPage.value = page
    const d = await getUserListByRoleId(roleId, String(page), '10')
    if (d && d.list) { authUsers.value = d.list; authUserTotal.value = Number(d.total || 0) }
  }
}

async function grantApi(row) {
  try {
    await authorityRoleResource({ roleId, resourceId: row.id })
    ElMessage.success(`已授权「${row.name}」`)
    loadApis(notAuthPage.value, 'not')
    loadApis(authPage.value, 'auth')
  } catch (e) { ElMessage.error(e.message) }
}

async function revokeApi(row) {
  try {
    await deleteAuthorityRoleResource({ roleId, resourceId: row.id })
    ElMessage.success(`已取消「${row.name}」`)
    loadApis(notAuthPage.value, 'not')
    loadApis(authPage.value, 'auth')
  } catch (e) { ElMessage.error(e.message) }
}

async function grantUser(row) {
  try {
    await authorityRoleUser({ roleId, resourceId: row.uid })
    ElMessage.success(`已关联「${row.uid}」`)
    loadUsers(notUserPage.value, 'not')
    loadUsers(authUserPage.value, 'auth')
  } catch (e) { ElMessage.error(e.message) }
}

async function revokeUser(row) {
  try {
    await deleteAuthorityRoleUser({ roleId, resourceId: row.uid })
    ElMessage.success(`已取消「${row.uid}」`)
    loadUsers(notUserPage.value, 'not')
    loadUsers(authUserPage.value, 'auth')
  } catch (e) { ElMessage.error(e.message) }
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
.sub-card { margin-bottom: 8px; }
</style>
