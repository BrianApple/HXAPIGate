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

    <el-row :gutter="16" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span class="chart-title"><el-icon color="#409eff"><PieChart /></el-icon> API 类型分布</span>
          </template>
          <div ref="typeChartRef" class="chart-box" v-loading="chartLoading" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span class="chart-title"><el-icon color="#67c23a"><DataAnalysis /></el-icon> API 接口状态</span>
          </template>
          <div ref="statusChartRef" class="chart-box" v-loading="chartLoading" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts/core'
import { PieChart } from 'echarts/charts'
import { TooltipComponent, LegendComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { getApiTypes, getRoleList, getApiList } from '../api'

echarts.use([PieChart, TooltipComponent, LegendComponent, CanvasRenderer])

const typeCount = ref('-')
const apiCount = ref('-')
const roleCount = ref('-')
const chartLoading = ref(false)
const typeChartRef = ref(null)
const statusChartRef = ref(null)

let typeChart = null
let statusChart = null

const COLORS = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#53c3a0', '#b37feb', '#ff9f43', '#36cfc9', '#597ef7']

function initCharts() {
  typeChart = echarts.init(typeChartRef.value)
  statusChart = echarts.init(statusChartRef.value)
}

function renderTypeChart(entries) {
  const sorted = entries.sort((a, b) => b[1] - a[1])
  typeChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} 个 ({d}%)' },
    legend: { bottom: 0, type: 'scroll', icon: 'circle' },
    color: COLORS,
    series: [{
      name: 'API 类型',
      type: 'pie',
      radius: ['35%', '65%'],
      center: ['50%', '45%'],
      avoidLabelOverlap: true,
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
      label: { formatter: '{b}\n{c} 个' },
      data: sorted.map(([name, value]) => ({ name, value }))
    }]
  })
}

function renderStatusChart(enabled, disabled) {
  const total = enabled + disabled
  statusChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} 个 ({d}%)' },
    legend: { bottom: 0, icon: 'circle' },
    color: ['#67c23a', '#c0c4cc'],
    series: [{
      name: '接口状态',
      type: 'pie',
      radius: ['35%', '65%'],
      center: ['50%', '45%'],
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
      label: { formatter: '{b}\n{c} 个' },
      data: [
        { name: '启用', value: enabled },
        { name: '停用', value: disabled }
      ],
      emphasis: {
        label: { show: true, fontWeight: 'bold' }
      }
    }]
  })
}

function onResize() {
  typeChart && typeChart.resize()
  statusChart && statusChart.resize()
}

onMounted(async () => {
  initCharts()
  window.addEventListener('resize', onResize)
  chartLoading.value = true
  try {
    const [topTypes, subTypes] = await Promise.all([
      getApiTypes('true').catch(() => []),
      getApiTypes('false').catch(() => [])
    ])
    const types = [...(topTypes || []), ...(subTypes || [])]
    typeCount.value = types.length
    const typeMap = new Map(types.map(t => [t.id, t.name]))

    const roles = await getRoleList(1, 1)
    if (roles && roles.total !== undefined) roleCount.value = roles.total

    const apis = await getApiList(-1, 1, 1000)
    const list = (apis && apis.list) || []
    if (apis && apis.total !== undefined) apiCount.value = apis.total
    else apiCount.value = list.length

    // 聚合：按 parentId 关联类型名
    const typeAgg = {}
    for (const a of list) {
      const name = (a.parentId != null && typeMap.get(a.parentId)) || '未分类'
      typeAgg[name] = (typeAgg[name] || 0) + 1
    }
    const entries = Object.entries(typeAgg)
    if (entries.length) renderTypeChart(entries)

    // 聚合：启用 / 停用
    const enabled = list.filter(a => a.status === 1).length
    const disabled = list.length - enabled
    renderStatusChart(enabled, disabled)
  } catch (e) {
    console.error('首页统计加载失败', e)
  } finally {
    chartLoading.value = false
  }
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', onResize)
  typeChart && typeChart.dispose()
  statusChart && statusChart.dispose()
})
</script>

<style scoped>
.welcome h2 { color: #1f3b73; margin-bottom: 8px; }
.welcome p { color: #666; margin-bottom: 12px; }
.stat-card { text-align: center; padding: 12px 0; }
.stat-num { font-size: 32px; font-weight: 700; color: #333; margin-top: 8px; }
.stat-label { color: #999; margin-top: 4px; }
.chart-row { margin-top: 16px; }
.chart-title { font-weight: 600; color: #333; display: inline-flex; align-items: center; gap: 6px; }
.chart-box { height: 320px; }
</style>
