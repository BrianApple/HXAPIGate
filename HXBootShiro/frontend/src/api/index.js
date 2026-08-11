import axios from 'axios'

// ---------- 登录态 ----------
export function getUserInfo() {
  try {
    return JSON.parse(localStorage.getItem('userInfo') || 'null')
  } catch {
    return null
  }
}
export function setUserInfo(info) {
  localStorage.setItem('userInfo', JSON.stringify(info))
}
export function clearUserInfo() {
  localStorage.removeItem('userInfo')
}

/**
 * 后端接口统一为 Spring MVC form 绑定（ReqWebData）：
 *   - jwt / userId / str / pageIndex / pageSize 为平铺参数
 *   - data 为嵌套 Map，编码为 data[key]=value
 * 响应统一为 { retSig: 200, httpRet: <数据> }，此处直接解包返回 httpRet。
 */
export async function postForm(url, { str, pageIndex, pageSize, data, extra = {} } = {}) {
  const user = getUserInfo()
  const body = new URLSearchParams()
  if (user) {
    body.append('jwt', user.jwt || '')
    body.append('userId', (user.user && user.user.uid) || 'admin')
  }
  if (str !== undefined && str !== null) body.append('str', str)
  if (pageIndex !== undefined) body.append('pageIndex', pageIndex)
  if (pageSize !== undefined) body.append('pageSize', pageSize)
  if (data && typeof data === 'object') {
    for (const [k, v] of Object.entries(data)) {
      if (v !== undefined && v !== null) body.append(`data[${k}]`, v)
    }
  }
  for (const [k, v] of Object.entries(extra)) {
    if (v !== undefined && v !== null) body.append(k, v)
  }
  const resp = await axios.post(url, body, {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
  })
  const ret = resp.data
  // 过滤器拦截时返回 {meta:{msg,code}} 结构（无 retSig），需兼容提取真实错误信息
  if (ret && ret.retSig === 200) {
    return ret.httpRet
  }
  const metaMsg = ret && ret.meta && ret.meta.msg
  throw new Error((ret && (ret.httpRet || ret.msg || metaMsg)) || '请求失败')
}

// ---------- 登录 ----------
export function login(appId, password) {
  const body = new URLSearchParams()
  body.append('data[appId]', appId)
  body.append('data[password]', password)
  return axios.post('/inner/user/login', body, {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
  }).then(resp => {
    const ret = resp.data
    if (ret && ret.retSig === 200 && ret.httpRet && ret.httpRet.meta && ret.httpRet.meta.code === 200) {
      return ret.httpRet.data
    }
    throw new Error('用户名或密码错误')
  })
}

// ---------- API 类型 ----------
export const getApiTypes = (str = 'true') => postForm('/inner/api/initApiType', { str })
export const addApiResource = (data, str) => postForm('/inner/api/addApiResource', { data, str })
export const updateApiResource = (data, str) => postForm('/inner/api/updateApiResource', { data, str })
export const deleteApiResource = (str) => postForm('/inner/api/deleteApiResource', { str })

// ---------- API 管理 ----------
export const getApiList = (str, pageIndex, pageSize) =>
  postForm('/inner/api/initApiByItemId', { str, pageIndex, pageSize })
export const getApiDetail = (str) => postForm('/inner/api/initApiByApiId', { str })

// ---------- 角色管理 ----------
export const getRoleList = (pageIndex, pageSize) =>
  postForm('/inner/role/initRole', { pageIndex, pageSize })
export const addRole = (data) => postForm('/inner/role/addRole', { data })
export const updateRoleById = (data, str) => postForm('/inner/role/updateRoleById', { data, str })
export const deleteRoleById = (str) => postForm('/inner/role/deleteRoleById', { str })
export const initRoleByroleId = (str) => postForm('/inner/role/initRoleByroleId', { str })

// ---------- 日志查询 ----------
export const searchLogs = (data, pageIndex, pageSize) =>
  postForm('/inner/log/search', { data, pageIndex, pageSize })
export const traceLogs = (str) => postForm('/inner/log/trace', { str })

// 角色-API 授权
export const getRestApiByRoleId = (str, pageIndex, pageSize) =>
  postForm('/inner/role/getRestApiByRoleId', { str, pageIndex, pageSize })
export const getRestApiExtendByRoleId = (str, pageIndex, pageSize) =>
  postForm('/inner/role/getRestApiExtendByRoleId', { str, pageIndex, pageSize })
export const authorityRoleResource = (data) => postForm('/inner/role/authorityRoleResource', { data })
export const deleteAuthorityRoleResource = (data) => postForm('/inner/role/deleteAuthorityRoleResource', { data })

// 角色-用户关联
export const getUserListByRoleId = (str, pageIndex, pageSize) =>
  postForm('/inner/role/getUserListByRoleId', { str, pageIndex, pageSize })
export const getUserListExtendByRoleId = (str, pageIndex, pageSize) =>
  postForm('/inner/role/getUserListExtendByRoleId', { str, pageIndex, pageSize })
export const authorityRoleUser = (data) => postForm('/inner/role/authorityRoleUser', { data })
export const deleteAuthorityRoleUser = (data) => postForm('/inner/role/deleteAuthorityRoleUser', { data })
