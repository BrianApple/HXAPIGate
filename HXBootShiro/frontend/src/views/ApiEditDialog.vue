<template>
  <el-dialog v-model="visible" :title="form.id ? '编辑API' : '新增API'" width="640px" destroy-on-close>
    <el-form :model="form" label-width="110px">
      <el-divider content-position="left">基本信息</el-divider>
      <el-form-item label="接口名称" required>
        <el-input v-model="form.interface_name" placeholder="如：查询设备数据" />
      </el-form-item>
      <el-form-item label="请求路径(URI)" required>
        <el-input v-model="form.url_val" placeholder="如：/device/query" />
      </el-form-item>
      <el-form-item label="所属类型" required>
        <el-select v-model="form.api_Type" style="width: 100%">
          <el-option v-for="t in types" :key="t.id" :label="t.name" :value="t.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="请求方式">
        <el-select v-model="form.request_method" style="width: 100%">
          <el-option v-for="m in ['GET', 'POST', 'PUT', 'DELETE']" :key="m" :label="m" :value="m" />
        </el-select>
      </el-form-item>
      <el-form-item label="API版本">
        <el-input v-model="form.api_version" placeholder="如：v1.0" />
      </el-form-item>
      <el-form-item label="状态">
        <el-radio-group v-model="form.state">
          <el-radio :value="1">启用</el-radio>
          <el-radio :value="0">停用</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="是否鉴权">
        <el-radio-group v-model="form.isAuth">
          <el-radio :value="1">是</el-radio>
          <el-radio :value="0">否</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-divider content-position="left">路由配置</el-divider>
      <el-form-item label="代理类型">
        <el-radio-group v-model="form.pType" @change="onPTypeChange">
          <el-radio value="http">HTTP</el-radio>
          <el-radio value="mcp">MCP</el-radio>
          <el-radio value="tcp">TCP</el-radio>
          <el-radio value="websocket">WebSocket</el-radio>
          <el-radio value="dubbo">Dubbo</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-if="form.pType === 'http'" label="暴露为MCP工具">
        <el-switch v-model="form.mcp_expose" active-value="1" inactive-value="0" />
        <span class="mcp-tip">开启后，MCP 客户端可通过网关 /mcp 端点调用此接口（自动协议转换）</span>
      </el-form-item>
      <el-form-item v-if="isNodeBased" label="负载均衡">
        <el-select v-model="form.balance" style="width: 100%">
          <el-option v-for="b in ['ROUND_ROBIN', 'RANDOM', 'WEIGHTED', 'TPS_LIMIT']" :key="b" :label="b" :value="b" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="isNodeBased" label="总TPS限制">
        <el-input v-model="form.all_tps" placeholder="如：1000" />
      </el-form-item>
      <el-form-item v-if="isNodeBased" label="单路由TPS">
        <el-input v-model="form.route_tps" placeholder="如：500" />
      </el-form-item>
      <template v-if="isNodeBased">
        <el-form-item v-for="(r, i) in routes" :key="i" :label="`后端节点${i + 1}`">
          <div class="route-row">
            <el-input v-model="r.ip" placeholder="IP" style="width: 150px" />
            <el-input v-model="r.port" placeholder="端口" style="width: 90px" />
            <el-input v-model="r.weight" placeholder="权重" style="width: 80px" />
            <el-input v-model="r.tps" placeholder="TPS" style="width: 90px" />
            <el-button link type="danger" :disabled="routes.length === 1" @click="routes.splice(i, 1)">移除</el-button>
          </div>
        </el-form-item>
        <el-form-item label=" ">
          <el-button link type="primary" @click="routes.push({ ip: '', port: '', weight: '', tps: '' })">+ 添加节点</el-button>
        </el-form-item>
      </template>
      <el-form-item v-if="form.pType === 'dubbo'" label="接口服务名">
        <el-input v-model="form.route_InterfaceName" placeholder="如：com.hx.api.DeviceService" />
      </el-form-item>

      <el-divider content-position="left">熔断配置（留空=网关按TPS自动推导）</el-divider>
      <el-form-item label="失败阈值">
        <el-input v-model="form.cb_fail_threshold" placeholder="如：10（连续失败N次后熔断）" />
      </el-form-item>
      <el-form-item label="恢复成功阈值">
        <el-input v-model="form.cb_success_threshold" placeholder="如：5（半开状态连续成功N次恢复）" />
      </el-form-item>
      <el-form-item label="熔断超时(ms)">
        <el-input v-model="form.cb_timeout" placeholder="如：3000（熔断打开持续时间）" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="saving" @click="onSave">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getApiTypes, getApiDetail, addApiResource, updateApiResource } from '../api'

const visible = ref(false)
const saving = ref(false)
const types = ref([])
const form = ref(emptyForm())
const routes = ref([{ ip: '', port: '', weight: '', tps: '' }])
// http/mcp/tcp/websocket 均为节点配置型协议（ip:port 列表 + 负载均衡 + TPS），dubbo 为接口服务名型
const isNodeBased = computed(() => ['http', 'mcp', 'tcp', 'websocket'].includes(form.value.pType))

function emptyForm() {
  return {
    id: null, interface_name: '', url_val: '', api_Type: null,
    request_method: 'POST', api_version: 'v1.0', state: 1, isAuth: 1,
    pType: 'http', balance: 'ROUND_ROBIN', all_tps: '', route_tps: '',
    mcp_expose: '0',
    route_InterfaceName: '',
    cb_fail_threshold: '', cb_success_threshold: '', cb_timeout: ''
  }
}

function open(row) {
  visible.value = true
  form.value = emptyForm()
  routes.value = [{ ip: '', port: '', weight: '', tps: '' }]
  if (row) {
    form.value.id = row.id
    form.value.interface_name = row.name
    form.value.url_val = row.uri
    form.value.api_Type = row.parentId
    form.value.request_method = row.method
    form.value.api_version = row.version || 'v1.0'
    form.value.state = Number(row.status)
    form.value.isAuth = Number(row.needAuth)
    // 路由详情回显
    getApiDetail(String(row.id)).then(d => {
      const info = d && d.routeInfo
      if (info && typeof info === 'object') {
        if (info.pType) form.value.pType = info.pType
        if (info.balance) form.value.balance = info.balance
        if (info.all_tps !== undefined) form.value.all_tps = info.all_tps
        if (info.route_tps !== undefined) form.value.route_tps = info.route_tps
        if (info.route_InterfaceName) form.value.route_InterfaceName = info.route_InterfaceName
        if (info.mcp_expose !== undefined) form.value.mcp_expose = String(info.mcp_expose)
        if (info.cb_fail_threshold !== undefined && info.cb_fail_threshold !== null) form.value.cb_fail_threshold = info.cb_fail_threshold
        if (info.cb_success_threshold !== undefined && info.cb_success_threshold !== null) form.value.cb_success_threshold = info.cb_success_threshold
        if (info.cb_timeout !== undefined && info.cb_timeout !== null) form.value.cb_timeout = info.cb_timeout
        const rs = []
        for (let i = 1; i <= (info.routeNum || 0); i++) {
          rs.push({
            ip: info[`rout_ipAddr${i}`] || '',
            port: info[`rout_port${i}`] || '',
            weight: info[`rout_weight${i}`] || '',
            tps: info[`rout_tps${i}`] || ''
          })
        }
        if (rs.length) routes.value = rs
      }
    }).catch(() => {})
  }
}

function onPTypeChange() {
  if (form.value.pType === 'dubbo') routes.value = [{ ip: '', port: '', weight: '', tps: '' }]
}

function buildData() {
  const d = {
    interface_name: form.value.interface_name,
    url_val: form.value.url_val,
    api_Type: String(form.value.api_Type),
    sour_Type: form.value.pType === 'dubbo' ? '2' : '1',
    request_method: form.value.request_method,
    api_version: form.value.api_version,
    state: String(form.value.state),
    isAuth: String(form.value.isAuth),
    pType: form.value.pType
  }
  if (form.value.pType === 'http' && form.value.mcp_expose === '1') {
    d.mcp_expose = '1'
  }
  if (isNodeBased.value) {
    d.balance = form.value.balance
    if (form.value.all_tps) d.all_tps = form.value.all_tps
    if (form.value.route_tps) d.route_tps = form.value.route_tps
    routes.value.forEach((r, i) => {
      const n = i + 1
      if (r.ip) d[`rout_ipAddr${n}`] = r.ip
      if (r.port) d[`rout_port${n}`] = r.port
      if (r.weight) d[`rout_weight${n}`] = r.weight
      if (r.tps) d[`rout_tps${n}`] = r.tps
    })
    d.routeNum = String(routes.value.length)
  } else {
    if (form.value.route_InterfaceName) d.route_InterfaceName = form.value.route_InterfaceName
  }
  // 熔断配置（可选）
  if (form.value.cb_fail_threshold !== '') d.cb_fail_threshold = form.value.cb_fail_threshold
  if (form.value.cb_success_threshold !== '') d.cb_success_threshold = form.value.cb_success_threshold
  if (form.value.cb_timeout !== '') d.cb_timeout = form.value.cb_timeout
  return d
}

async function onSave() {
  if (!form.value.interface_name || !form.value.url_val || !form.value.api_Type) {
    ElMessage.warning('请填写完整基本信息')
    return
  }
  saving.value = true
  try {
    const data = buildData()
    if (form.value.id) {
      await updateApiResource(data, String(form.value.id))
      ElMessage.success('更新成功')
    } else {
      await addApiResource(data)
      ElMessage.success('新增成功')
    }
    visible.value = false
    emit('saved')
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    saving.value = false
  }
}

const emit = defineEmits(['saved'])
onMounted(async () => {
  try {
    // 下拉框需包含全部 type=3 类型（根类型110 + 子类型103/219），
    // initApiType: str=true 返回根类型(parent_id=-1)，str=false 返回子类型
    const [roots, subs] = await Promise.all([getApiTypes('true'), getApiTypes('false')])
    const map = new Map()
    ;[...(Array.isArray(roots) ? roots : []), ...(Array.isArray(subs) ? subs : [])].forEach(t => map.set(t.id, t))
    types.value = [...map.values()]
  } catch {}
})
defineExpose({ open })
</script>

<style scoped>
.route-row { display: flex; gap: 8px; width: 100%; align-items: center; }
.mcp-tip { margin-left: 8px; font-size: 12px; color: #909399; }
</style>
