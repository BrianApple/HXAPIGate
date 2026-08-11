<template>
  <div class="login-wrap">
    <!-- 背景层：渐变 + 网格 + 光晕 -->
    <div class="bg-grad"></div>
    <div class="bg-grid"></div>
    <div class="bg-glow glow-1"></div>
    <div class="bg-glow glow-2"></div>
    <!-- 粒子网络动画（API 网关分布式节点拓扑） -->
    <canvas ref="canvasRef" class="bg-particles"></canvas>

    <el-card class="login-card">
      <div class="logo-badge">
        <img :src="logoUrl" alt="HXAPIGate" class="logo-img" />
      </div>
      <h2 class="title">HXAPIGate 管理平台</h2>
      <p class="subtitle">统一 API 网关与鉴权管理控制台</p>
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
      <p class="footer-tip">© 2019-2026 HXAPIGate · 基于 Shiro 的统一鉴权网关</p>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { login, setUserInfo } from '../api'
import logoUrl from '../assets/hxapi_logo.png'

const router = useRouter()
const form = reactive({ appId: '', password: '' })
const loading = ref(false)

const canvasRef = ref(null)
let raf = 0
let particles = []
const mouse = { x: -9999, y: -9999 }

function initParticles(canvas) {
  const ctx = canvas.getContext('2d')
  const dpr = window.devicePixelRatio || 1
  let W = 0, H = 0
  const LINK_DIST = 150

  function resize() {
    W = canvas.clientWidth
    H = canvas.clientHeight
    canvas.width = W * dpr
    canvas.height = H * dpr
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0)
    const count = Math.min(110, Math.max(60, Math.floor((W * H) / 16000)))
    particles = Array.from({ length: count }, () => ({
      x: Math.random() * W,
      y: Math.random() * H,
      vx: (Math.random() - 0.5) * 0.45,
      vy: (Math.random() - 0.5) * 0.45,
      r: Math.random() * 1.8 + 1,
    }))
  }

  function draw() {
    ctx.clearRect(0, 0, W, H)
    for (const p of particles) {
      p.x += p.vx
      p.y += p.vy
      if (p.x < 0 || p.x > W) p.vx *= -1
      if (p.y < 0 || p.y > H) p.vy *= -1
    }
    // 粒子间连线
    for (let i = 0; i < particles.length; i++) {
      const a = particles[i]
      for (let j = i + 1; j < particles.length; j++) {
        const b = particles[j]
        const dx = a.x - b.x, dy = a.y - b.y
        const d = Math.hypot(dx, dy)
        if (d < LINK_DIST) {
          const alpha = (1 - d / LINK_DIST) * 0.42
          ctx.strokeStyle = `rgba(130, 185, 255, ${alpha})`
          ctx.lineWidth = 1
          ctx.beginPath()
          ctx.moveTo(a.x, a.y)
          ctx.lineTo(b.x, b.y)
          ctx.stroke()
        }
      }
      // 与鼠标连线（高亮）
      const dm = Math.hypot(a.x - mouse.x, a.y - mouse.y)
      if (dm < LINK_DIST * 1.3) {
        const alpha = (1 - dm / (LINK_DIST * 1.3)) * 0.55
        ctx.strokeStyle = `rgba(255, 255, 255, ${alpha})`
        ctx.lineWidth = 1
        ctx.beginPath()
        ctx.moveTo(a.x, a.y)
        ctx.lineTo(mouse.x, mouse.y)
        ctx.stroke()
      }
    }
    // 粒子点
    for (const p of particles) {
      ctx.fillStyle = 'rgba(168, 205, 255, 0.9)'
      ctx.beginPath()
      ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2)
      ctx.fill()
    }
    raf = requestAnimationFrame(draw)
  }

  function onMouseMove(e) {
    const rect = canvas.getBoundingClientRect()
    mouse.x = e.clientX - rect.left
    mouse.y = e.clientY - rect.top
  }
  function onMouseLeave() {
    mouse.x = -9999
    mouse.y = -9999
  }

  resize()
  window.addEventListener('resize', resize)
  canvas.addEventListener('mousemove', onMouseMove)
  canvas.addEventListener('mouseleave', onMouseLeave)
  draw()

  return () => {
    cancelAnimationFrame(raf)
    window.removeEventListener('resize', resize)
    canvas.removeEventListener('mousemove', onMouseMove)
    canvas.removeEventListener('mouseleave', onMouseLeave)
  }
}

let cleanupFn = null

onMounted(() => {
  if (canvasRef.value) {
    cleanupFn = initParticles(canvasRef.value)
  }
})

onBeforeUnmount(() => {
  if (cleanupFn) cleanupFn()
})

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
  position: relative;
  height: 100%;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}
/* 底层深蓝渐变 */
.bg-grad {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #0d1b3a 0%, #142c5c 35%, #1f4e96 70%, #2d6fbf 100%);
}
/* 细网格线 */
.bg-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(120, 170, 255, 0.06) 1px, transparent 1px),
    linear-gradient(90deg, rgba(120, 170, 255, 0.06) 1px, transparent 1px);
  background-size: 44px 44px;
  mask-image: radial-gradient(ellipse at center, rgba(0,0,0,0.9) 0%, transparent 75%);
  -webkit-mask-image: radial-gradient(ellipse at center, rgba(0,0,0,0.9) 0%, transparent 75%);
}
/* 光晕 */
.bg-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(90px);
  opacity: 0.45;
  pointer-events: none;
}
.glow-1 {
  width: 480px;
  height: 480px;
  left: -120px;
  top: -140px;
  background: radial-gradient(circle, rgba(64, 140, 255, 0.55), transparent 70%);
}
.glow-2 {
  width: 560px;
  height: 560px;
  right: -160px;
  bottom: -180px;
  background: radial-gradient(circle, rgba(40, 200, 220, 0.35), transparent 70%);
}
/* 粒子层 */
.bg-particles {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  pointer-events: auto;
}
/* 登录卡片：玻璃拟态 */
.login-card {
  position: relative;
  z-index: 2;
  width: 400px;
  padding: 28px 18px 18px;
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.35);
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(10px);
  box-shadow: 0 18px 50px rgba(3, 15, 40, 0.45);
}
.logo-badge {
  width: 92px;
  height: 92px;
  margin: 0 auto 12px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  border: 1px solid rgba(22, 48, 92, 0.08);
  box-shadow: 0 10px 28px rgba(45, 111, 191, 0.28);
  overflow: hidden;
  padding: 10px;
}
.logo-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
}
.title {
  text-align: center;
  color: #16305c;
  margin: 0 0 6px;
  font-size: 21px;
  font-weight: 600;
  letter-spacing: 1px;
}
.subtitle {
  text-align: center;
  color: #7a8aa5;
  font-size: 12.5px;
  margin: 0 0 22px;
}
.footer-tip {
  text-align: center;
  color: #a0aabf;
  font-size: 11.5px;
  margin: 16px 0 0;
}
</style>
