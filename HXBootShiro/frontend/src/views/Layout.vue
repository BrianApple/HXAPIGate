<template>
  <el-container class="layout">
    <el-aside width="210px" class="aside">
      <div class="logo">HXAPIGate</div>
      <el-menu :default-active="$route.path" router background-color="#1f2d3d" text-color="#bfcbd9" active-text-color="#409eff">
        <el-menu-item index="/welcome"><el-icon><HomeFilled /></el-icon><span>首页</span></el-menu-item>
        <el-menu-item index="/apiType"><el-icon><FolderOpened /></el-icon><span>API类型管理</span></el-menu-item>
        <el-menu-item index="/api"><el-icon><Connection /></el-icon><span>API管理</span></el-menu-item>
        <el-menu-item index="/log"><el-icon><Document /></el-icon><span>日志查询</span></el-menu-item>
        <el-menu-item index="/role"><el-icon><UserFilled /></el-icon><span>角色管理</span></el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span class="page-title">{{ $route.meta.title || '' }}</span>
        <el-dropdown @command="onCommand">
          <span class="user-info">
            <el-icon><User /></el-icon>
            {{ userName }}
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { getUserInfo, clearUserInfo } from '../api'

const router = useRouter()
const userInfo = getUserInfo()
const userName = computed(() => (userInfo && userInfo.user && (userInfo.user.username || userInfo.user.uid)) || 'admin')

function onCommand(cmd) {
  if (cmd === 'logout') {
    clearUserInfo()
    router.push('/login')
  }
}
</script>

<style scoped>
.layout { height: 100%; }
.aside { background: #1f2d3d; }
.aside .el-menu { border-right: none; }
.logo {
  height: 60px; line-height: 60px; text-align: center; color: #fff;
  font-size: 18px; font-weight: 700; letter-spacing: 2px;
  background: #17222e;
}
.header {
  display: flex; align-items: center; justify-content: space-between;
  background: #fff; border-bottom: 1px solid #e6e6e6;
}
.page-title { font-size: 16px; font-weight: 600; color: #333; }
.user-info { display: flex; align-items: center; gap: 4px; cursor: pointer; color: #333; }
.main { background: #f0f2f5; padding: 16px; }
</style>
