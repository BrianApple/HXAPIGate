<template>
  <div class="log-search">
    <!-- 搜索区 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="query" inline @submit.prevent>
        <el-form-item label="TraceId">
          <el-input v-model="query.traceId" placeholder="请求溯源ID，支持模糊" clearable style="width: 220px" @keyup.enter="search(1)" />
        </el-form-item>
        <el-form-item label="帧号">
          <el-input v-model="query.frameId" placeholder="如 abc123-F0001，帧级链路" clearable style="width: 200px" @keyup.enter="search(1)" />
        </el-form-item>
        <el-form-item label="协议">
          <el-select v-model="query.proto" placeholder="全部" clearable style="width: 130px">
            <el-option v-for="p in ['http', 'mcp', 'websocket', 'dubbo']" :key="p" :label="p" :value="p" />
          </el-select>
        </el-form-item>
        <el-form-item label="级别">
          <el-select v-model="query.level" placeholder="全部" clearable style="width: 110px">
            <el-option v-for="l in ['DEBUG', 'INFO', 'WARN', 'ERROR']" :key="l" :label="l" :value="l" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="匹配 logger 或消息内容" clearable style="width: 200px" @keyup.enter="search(1)" />
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 360px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="search(1)"><el-icon><Search /></el-icon>查询</el-button>
          <el-button @click="reset"><el-icon><RefreshLeft /></el-icon>重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 日志列表 -->
    <el-card shadow="never" class="list-card">
      <template #header>
        <div class="card-head">
          <span>日志记录（<b>{{ total }}</b> 条，来自网关 + 管理端）</span>
          <el-button size="small" :loading="loading" @click="search(pageIndex)">刷新</el-button>
        </div>
      </template>
      <el-table :data="logs" v-loading="loading" border stripe size="small" max-height="calc(100vh - 380px)">
        <el-table-column prop="time" label="时间" width="175" fixed="left" />
        <el-table-column label="级别" width="72">
          <template #default="{ row }">
            <el-tag size="small" :type="levelType(row.level)" disable-transitions>{{ row.level }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="协议" width="95">
          <template #default="{ row }">
            <el-tag v-if="row.proto" size="small" :type="protoType(row.proto)" effect="plain" disable-transitions>{{ row.proto }}</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="TraceId" width="160">
          <template #default="{ row }">
            <span v-if="row.traceId" class="trace-id">{{ row.traceId }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="帧号" width="175">
          <template #default="{ row }">
            <span v-if="row.frameId" class="frame-id" @click="openTrace(row.frameId)">{{ row.frameId }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="来源" width="75">
          <template #default="{ row }">
            <el-tag size="small" :type="row.source === 'gateway' ? 'primary' : 'success'" effect="plain" disable-transitions>
              {{ row.source === 'gateway' ? '网关' : '管理端' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="logger" label="Logger" width="200" show-overflow-tooltip />
        <el-table-column prop="message" label="消息" min-width="240" show-overflow-tooltip />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row)">详情</el-button>
            <el-button v-if="row.traceId" link type="primary" @click="openTrace(row.traceId)">链路</el-button>
            <el-button v-if="row.frameId" link type="warning" @click="openTrace(row.frameId)">帧链路</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pager-bar">
        <el-pagination
          background layout="total, sizes, prev, pager, next"
          :total="total" :page-size="pageSize" :current-page="pageIndex"
          :page-sizes="[20, 50, 100]" @current-change="search" @size-change="onSizeChange" />
      </div>
    </el-card>

    <!-- 请求链路抽屉 -->
    <el-drawer v-model="traceVisible" :title="`请求链路 · ${currentTraceId}`" size="52%" destroy-on-close>
      <div class="trace-head">
        <el-tag size="small" type="primary" effect="plain">{{ traceEntries.length }} 条日志</el-tag>
        <el-tag v-if="traceHasError" size="small" type="danger" effect="plain">含 ERROR</el-tag>
        <el-tag v-if="isFrameTrace" size="small" type="warning" effect="plain">帧级链路</el-tag>
        <span class="trace-tip">按时间升序展示网关与管理端中该 ID（traceId 或帧号）的完整处理路径</span>
      </div>
      <el-timeline v-if="traceEntries.length" class="trace-timeline">
        <el-timeline-item
          v-for="(item, i) in traceEntries" :key="i"
          :timestamp="item.time" placement="top"
          :type="item.level === 'ERROR' ? 'danger' : item.level === 'WARN' ? 'warning' : 'primary'"
        >
          <div class="trace-item" :class="{ 'is-error': item.level === 'ERROR' }">
            <div class="trace-meta">
              <el-tag size="small" :type="levelType(item.level)" disable-transitions>{{ item.level }}</el-tag>
              <el-tag size="small" effect="plain" :type="item.source === 'gateway' ? 'primary' : 'success'">{{ item.source === 'gateway' ? '网关' : '管理端' }}</el-tag>
              <el-tag v-if="item.proto" size="small" effect="plain" :type="protoType(item.proto)">{{ item.proto }}</el-tag>
              <el-tag v-if="item.frameId" size="small" effect="plain" type="warning">{{ item.frameId }}</el-tag>
              <span class="trace-logger">{{ item.logger }}</span>
            </div>
            <div class="trace-msg">{{ item.message }}</div>
          </div>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="未找到该 ID 的日志记录（可能已超过 90 天保留期）" />
    </el-drawer>

    <!-- 日志详情弹窗：完整消息内容 -->
    <el-dialog v-model="detailVisible" title="日志详情" width="760px" destroy-on-close>
      <div v-if="currentDetail" class="detail-body">
        <div class="detail-row"><span class="detail-label">时间</span><span class="detail-value">{{ currentDetail.time }}</span></div>
        <div class="detail-row"><span class="detail-label">级别</span><span class="detail-value">{{ currentDetail.level }}</span></div>
        <div class="detail-row"><span class="detail-label">协议</span><span class="detail-value">{{ currentDetail.proto || '-' }}</span></div>
        <div class="detail-row"><span class="detail-label">TraceId</span><span class="detail-value mono">{{ currentDetail.traceId || '-' }}</span></div>
        <div class="detail-row"><span class="detail-label">帧号</span><span class="detail-value mono">{{ currentDetail.frameId || '-' }}</span></div>
        <div class="detail-row"><span class="detail-label">来源</span><span class="detail-value">{{ currentDetail.source === 'gateway' ? '网关' : '管理端' }}</span></div>
        <div class="detail-row"><span class="detail-label">Logger</span><span class="detail-value mono">{{ currentDetail.logger }}</span></div>
        <div class="detail-row detail-msg-row"><span class="detail-label">消息</span><pre class="detail-msg">{{ currentDetail.message }}</pre></div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, RefreshLeft } from '@element-plus/icons-vue'
import { searchLogs, traceLogs } from '../api'

const loading = ref(false)
const logs = ref([])
const total = ref(0)
const pageIndex = ref(1)
const pageSize = ref(20)
const timeRange = ref(null)

const query = ref({ traceId: '', frameId: '', proto: '', level: '', keyword: '' })

const traceVisible = ref(false)
const currentTraceId = ref('')
const traceData = ref([])
const traceHasError = computed(() => traceData.value.some(x => x.level === 'ERROR'))
const traceEntries = computed(() => traceData.value)
// 帧模式：查询 ID 形如 <traceId>-F<序号> 时为帧级链路
const isFrameTrace = computed(() => /-F\d+$/.test(currentTraceId.value))

// 日志详情弹窗
const detailVisible = ref(false)
const currentDetail = ref(null)
function openDetail(row) {
  currentDetail.value = row
  detailVisible.value = true
}

async function search(page = pageIndex.value) {
  pageIndex.value = page
  loading.value = true
  try {
    const data = {
      traceId: query.value.traceId || '',
      frameId: query.value.frameId || '',
      proto: query.value.proto || '',
      level: query.value.level || '',
      keyword: query.value.keyword || '',
      startTime: timeRange.value ? timeRange.value[0] : '',
      endTime: timeRange.value ? timeRange.value[1] : ''
    }
    const ret = await searchLogs(data, String(page), String(pageSize.value))
    if (ret && ret.list) {
      logs.value = ret.list
      total.value = Number(ret.total || 0)
    } else {
      logs.value = []
      total.value = 0
    }
  } catch (e) {
    ElMessage.error(e.message || '查询失败')
  } finally {
    loading.value = false
  }
}

function reset() {
  query.value = { traceId: '', frameId: '', proto: '', level: '', keyword: '' }
  timeRange.value = null
  search(1)
}

function onSizeChange(size) {
  pageSize.value = size
  search(1)
}

async function openTrace(traceId) {
  currentTraceId.value = traceId
  traceVisible.value = true
  traceData.value = []
  try {
    const list = await traceLogs(traceId)
    traceData.value = Array.isArray(list) ? list : []
  } catch (e) {
    ElMessage.error(e.message || '链路查询失败')
  }
}

function levelType(level) {
  return level === 'ERROR' ? 'danger' : level === 'WARN' ? 'warning' : level === 'DEBUG' ? 'info' : 'success'
}

function protoType(proto) {
  return proto === 'websocket' ? 'primary' : proto === 'mcp' ? 'warning' : ''
}

search(1)
</script>

<style scoped>
.search-card { margin-bottom: 14px; }
.search-card :deep(.el-form-item) { margin-bottom: 8px; }
.card-head { display: flex; align-items: center; justify-content: space-between; }
.pager { margin-top: 12px; justify-content: flex-end; }
.trace-id { font-family: monospace; color: #409eff; cursor: pointer; }
.frame-id { font-family: monospace; color: #e6a23c; cursor: pointer; }
.trace-head { display: flex; align-items: center; gap: 8px; margin-bottom: 16px; }
.trace-tip { font-size: 12px; color: #999; }
.trace-timeline { padding-left: 4px; }
.trace-item { padding: 8px 10px; background: #f8f9fb; border-radius: 6px; border-left: 3px solid #409eff; }
.trace-item.is-error { background: #fef0f0; border-left-color: #f56c6c; }
.trace-meta { display: flex; align-items: center; gap: 6px; margin-bottom: 6px; flex-wrap: wrap; }
.trace-logger { font-size: 12px; color: #666; font-family: monospace; }
.trace-msg { font-size: 13px; color: #333; line-height: 1.6; word-break: break-all; }
.pager-bar { margin-top: 12px; display: flex; justify-content: flex-end; }
.detail-body { max-height: 60vh; overflow-y: auto; }
.detail-row { display: flex; margin-bottom: 10px; font-size: 13px; }
.detail-label { width: 70px; color: #909399; flex-shrink: 0; line-height: 22px; }
.detail-value { color: #303133; line-height: 22px; word-break: break-all; }
.detail-value.mono { font-family: monospace; }
.detail-msg-row { align-items: flex-start; }
.detail-msg { margin: 0; padding: 10px 12px; background: #f8f9fb; border-radius: 6px; font-size: 13px; line-height: 1.7; white-space: pre-wrap; word-break: break-all; width: 100%; color: #303133; font-family: monospace; }
</style>
