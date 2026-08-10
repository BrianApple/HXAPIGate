<template>
  <div class="welcome">
    <el-card>
      <h2>欢迎使用 HXAPIGate 管理平台</h2>
      <p>统一 API 网关与鉴权管理控制台，用于管理 API 资源、角色授权与网关路由配置。</p>
      <el-divider />
      <el-row :gutter="16">
        <el-col :span="8">
          <el-card shadow="hover" class="stat-card">
            <el-icon color="#409eff" :size="36"><FolderOpened /></el-icon>
            <div class="stat-num">{{ typeCount }}</div>
            <div class="stat-label">API 类型</div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="stat-card">
            <el-icon color="#67c23a" :size="36"><Connection /></el-icon>
            <div class="stat-num">{{ apiCount }}</div>
            <div class="stat-label">API 接口</div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="stat-card">
            <el-icon color="#e6a23c" :size="36"><UserFilled /></el-icon>
            <div class="stat-num">{{ roleCount }}</div>
            <div class="stat-label">角色</div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getApiTypes, getRoleList, getApiList } from '../api'

const typeCount = ref('-')
const apiCount = ref('-')
const roleCount = ref('-')

onMounted(async () => {
  try {
    const types = await getApiTypes()
    typeCount.value = Array.isArray(types) ? types.length : 0
  } catch {}
  try {
    const roles = await getRoleList(1, 1)
    if (roles && roles.total !== undefined) roleCount.value = roles.total
  } catch {}
  try {
    const apis = await getApiList(-1, 1, 1)
    if (apis && apis.total !== undefined) apiCount.value = apis.total
  } catch {}
})
</script>

<style scoped>
.welcome h2 { color: #1f3b73; margin-bottom: 8px; }
.welcome p { color: #666; margin-bottom: 12px; }
.stat-card { text-align: center; padding: 12px 0; }
.stat-num { font-size: 32px; font-weight: 700; color: #333; margin-top: 8px; }
.stat-label { color: #999; margin-top: 4px; }
</style>
