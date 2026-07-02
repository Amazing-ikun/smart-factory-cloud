import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const service = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json;charset=UTF-8' }
})

service.interceptors.request.use(
  config => {
    const userId = sessionStorage.getItem('userId')
    if (userId) {
      config.headers['X-User-Id'] = userId
    }
    const userName = sessionStorage.getItem('username')
    if (userName) {
      config.headers['X-User-Name'] = userName
    }
    // 上传文件时让浏览器自动设置 Content-Type（含 boundary）
    if (config.data instanceof FormData) {
      delete config.headers['Content-Type']
    }
    return config
  },
  error => Promise.reject(error)
)

service.interceptors.response.use(
  response => {
    const data = response.data
    if (data && data.success === false) {
      return Promise.reject({ ...response, data })
    }
    return Promise.resolve(response)
  },
  error => {
    if (error.response) {
      const status = error.response.status
      const data = error.response.data
      switch (status) {
        case 401:
          ElMessage.error('登录已过期，请重新登录')
          sessionStorage.clear()
          router.replace({ path: '/' })
          break
        case 403:
          ElMessage.error('没有权限访问')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(data?.message || `请求失败 (${status})`)
      }
    } else if (error.request) {
      ElMessage.error('网络连接失败，请检查网络设置')
    }
    return Promise.reject(error)
  }
)

export function get<T = any>(url: string, params?: object): Promise<T> {
  return service.get(url, { params }).then(r => r.data)
}

export function post<T = any>(url: string, data?: object): Promise<T> {
  return service.post(url, data).then(r => r.data)
}

export function put<T = any>(url: string, data?: object): Promise<T> {
  return service.put(url, data).then(r => r.data)
}

export function deletes<T = any>(url: string, data?: object): Promise<T> {
  return service.delete(url, { data }).then(r => r.data)
}

export const API_BASE = 'http://localhost:9999/api'

export function imageUrl(path: string | null | undefined): string {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return 'http://localhost:9999' + path
}

export function upload<T = any>(url: string, formData: FormData): Promise<T> {
  return service.post(url, formData).then(r => r.data)
}

export function download(url: string) {
  return service.get(url, { responseType: 'blob' }).then(r => {
    const disposition = r.headers['content-disposition'] || ''
    const match = disposition.match(/filename\*?=(?:UTF-8'')?([^;\n]+)/i)
    const name = match ? decodeURIComponent(match[1]) : 'export.xlsx'
    const a = document.createElement('a')
    a.href = URL.createObjectURL(r.data)
    a.download = name
    a.click()
    URL.revokeObjectURL(a.href)
  })
}

export default service
