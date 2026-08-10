<template>
  <div class="login-wrap">
    <el-card class="login-card">
      <h2 class="title">HXAPIGate 管理平台</h2>
      <el-form :model="form" @keyup.enter="onLogin">
        <el-form-item>
          <el-input v-model="form.appId" placeholder="用户名" size="large" :prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password :prefix-icon="Lock" />
        </el-form-item>
        <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="onLogin">
          登 录
        </el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { login, setUserInfo } from '../api'

const router = useRouter()
const form = reactive({ appId: '', password: '' })
const loading = ref(false)

async function onLogin() {
  if (!form.appId || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const data = await login(form.appId, form.password)
    setUserInfo(data)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (e) {
    ElMessage.error(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-wrap {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1f3b73 0%, #2d5aa8 50%, #4a8ce0 100%);
}
.login-card {
  width: 380px;
  padding: 20px 10px;
  border-radius: 10px;
}
.title {
  text-align: center;
  color: #1f3b73;
  margin-bottom: 24px;
  font-size: 20px;
}
</style>
