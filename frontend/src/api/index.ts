import { get, post, put, deletes, upload, download } from './request'

const BASE_URL = '/api'

const HttpManager = {
  // 用户
  userLogin: (data: { username: string; password: string }) => post('/user/login', data),
  userRegister: (data: any) => post('/user/register', data),
  createUser: (data: any) => post('/user/create', data),
  getUserList: (page?: number, pageSize?: number) => get('/user/list', { page, pageSize }),
  getUserById: (id: number) => get(`/user/${id}`),
  getUserByUserName: (name: string) => get(`/user/username/${name}`),
  getUsersByFactoryId: (id: number) => get(`/user/factory/${id}`),
  getUsersByStatus: (status: number) => get(`/user/status/${status}`),
  getUserStatusEnums: () => get('/user/status-enums'),
  updateUser: (data: any) => put('/user', data),
  deleteUser: (id: number) => deletes(`/user/${id}`),
  resetUserPassword: (id: number, password: string) => put(`/user/${id}/password`, { password }),

  // 工厂
  getFactoryList: () => get('/factory/list'),
  getFactoryById: (id: number) => get(`/factory/${id}`),

  // 设备
  getEquipmentList: (page?: number, pageSize?: number) => get('/equipment/list', { page, pageSize }),
  getEquipmentById: (id: number) => get(`/equipment/${id}`),
  getEquipmentsByFactoryId: (id: number) => get(`/equipment/factory/${id}`),
  getEquipmentsByStatus: (status: number) => get(`/equipment/status/${status}`),
  getEquipmentStatusEnums: () => get('/equipment/status-enums'),
  addEquipment: (data: any) => post('/equipment', data),
  updateEquipment: (data: any) => put('/equipment', data),
  deleteEquipment: (id: number) => deletes(`/equipment/${id}`),
  uploadEquipmentImage: (id: number, file: File) => {
    const fd = new FormData()
    fd.append('file', file)
    return upload(`/equipment/upload/${id}`, fd)
  },

  // 产品
  getProductList: (page?: number, pageSize?: number) => get('/product/list', { page, pageSize }),
  getProductById: (id: number) => get(`/product/${id}`),
  addProduct: (data: any) => post('/product', data),
  updateProduct: (data: any) => put('/product', data),
  deleteProduct: (id: number) => deletes(`/product/${id}`),
  uploadProductImage: (id: number, file: File) => {
    const fd = new FormData()
    fd.append('file', file)
    return upload(`/product/upload/${id}`, fd)
  },

  // 设备-产品映射
  getEquipmentProductList: () => get('/equipment-product/list'),
  addEquipmentProduct: (data: any) => post('/equipment-product', data),
  deleteEquipmentProduct: (id: number) => deletes(`/equipment-product/${id}`),

  // Excel 导入导出
  exportProductExcel: () => download('/product/export'),
  importProductExcel: (file: File) => {
    const fd = new FormData()
    fd.append('file', file)
    return upload('/product/import', fd)
  },
  exportEquipmentExcel: () => download('/equipment/export'),
  importEquipmentExcel: (file: File) => {
    const fd = new FormData()
    fd.append('file', file)
    return upload('/equipment/import', fd)
  },
  exportOrderExcel: () => download('/order/export'),
  importOrderExcel: (file: File) => {
    const fd = new FormData()
    fd.append('file', file)
    return upload('/order/import', fd)
  },

  // 订单
  getOrderList: (page?: number, pageSize?: number) => get('/order/list', { page, pageSize }),
  getOrderById: (id: number) => get(`/order/${id}`),
  getOrdersByFactoryId: (id: number) => get(`/order/factory/${id}`),
  getOrdersByStatus: (status: number) => get(`/order/status/${status}`),
  addOrder: (data: any) => post('/order', data),
  updateOrder: (data: any) => put('/order', data),
  updateOrderStatus: (id: number, status: number) => put(`/order/${id}/status`, { status }),
  deleteOrder: (id: number) => deletes(`/order/${id}`),
  getOrderStatusEnums: () => get('/order/status-enums'),

  // 生产计划
  getPlanList: () => get('/plan/list'),
  getPlanById: (id: number) => get(`/plan/${id}`),
  addPlan: (data: any) => post('/plan', data),
  updatePlan: (data: any) => put('/plan', data),
  deletePlan: (id: number) => deletes(`/plan/${id}`),

  // 生产调度
  getScheduleList: () => get('/schedule/list'),
  getScheduleById: (id: number) => get(`/schedule/${id}`),
  addSchedule: (data: any) => post('/schedule', data),
  updateSchedule: (id: number, data: any) => put(`/schedule/${id}`, data),
  deleteSchedule: (id: number) => deletes(`/schedule/${id}`),

  // 日报工
  getDailyWorkList: (page?: number, pageSize?: number) => get('/daily-work/list', { page, pageSize }),

  // 订单跟踪
  getOrderTrackList: (page?: number, pageSize?: number) => get('/order-track/list', { page, pageSize }),
  getOrderTrackById: (id: number) => get(`/order-track/${id}`),

  // 数据看板
  getDashboardStats: () => get('/stats/dashboard'),

  // 个人中心
  getUserProfile: (id: number) => get(`/user/${id}/profile`),
  updateUserIntro: (id: number, userIntro: string) => put(`/user/${id}/intro`, { userIntro }),
  uploadUserAvatar: (id: number, file: File) => {
    const fd = new FormData()
    fd.append('file', file)
    return upload(`/user/${id}/avatar`, fd)
  },
  getAvatarHistory: (id: number) => get(`/user/${id}/avatar-history`),
  deleteAvatarHistory: (historyId: number) => deletes(`/user/avatar-history/${historyId}`),
  deleteAvatarHistoryBatch: (ids: number[]) => deletes(`/user/avatar-history/batch`, { ids }),

  // 业务日志
  getLogList: (page?: number, pageSize?: number) => get('/log/list', { page, pageSize }),

  // 反馈
  getFeedbackList: (page?: number, pageSize?: number) => get('/feedback/list', { page, pageSize }),
  getMyFeedback: () => get('/feedback/my'),
  addFeedback: (data: any) => post('/feedback', data),
  replyFeedback: (data: any) => post('/feedback/reply', data),
  uploadFeedbackFile: (file: File) => {
    const fd = new FormData()
    fd.append('file', file)
    return upload('/feedback/upload', fd)
  },

  // 管理员申请
  getApplicationList: (page?: number, pageSize?: number) => get('/application/list', { page, pageSize }),
  getMyApplication: () => get('/application/my'),
  addApplication: (data: any) => post('/application', data),
  reviewApplication: (data: any) => post('/application/review', data),

  // 消息
  getMessageList: (page?: number, pageSize?: number) => get('/message/list', { page, pageSize }),
  getUnreadCount: () => get('/message/unread'),
  markMessageRead: (id: number) => post(`/message/read/${id}`, {}),
  sendMessage: (data: any) => post('/message/send', data),

  // 滑块验证码
  getCaptcha: () => get('/captcha/generate'),
  verifyCaptcha: (data: any) => post('/captcha/verify', data),
}

export { HttpManager }
