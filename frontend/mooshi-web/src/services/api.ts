import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios'
import { useAuthStore } from '@/store/auth'

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = useAuthStore.getState().accessToken
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    if (error.response?.status === 401) {
      const refreshToken = useAuthStore.getState().refreshToken
      if (refreshToken && error.config && !(error.config as any)._retry) {
        (error.config as any)._retry = true
        try {
          const res = await axios.post(
            `${process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api'}/auth/refresh`,
            { refreshToken }
          )
          const { accessToken, refreshToken: newRefresh } = res.data.data
          useAuthStore.getState().setTokens(accessToken, newRefresh)
          if (error.config.headers) {
            error.config.headers.Authorization = `Bearer ${accessToken}`
          }
          return api(error.config)
        } catch {
          useAuthStore.getState().logout()
        }
      }
    }
    return Promise.reject(error)
  }
)

// Auth
export const authApi = {
  register: (data: { email: string; password: string; firstName: string; lastName: string }) =>
    api.post('/auth/register', data),
  login: (data: { email: string; password: string }) =>
    api.post('/auth/login', data),
  refresh: (refreshToken: string) =>
    api.post('/auth/refresh', { refreshToken }),
}

// Catalog
export const catalogApi = {
  getCategories: () => api.get('/v1/catalog/categories'),
  getProducts: (params?: { category?: string; search?: string; popular?: boolean }) =>
    api.get('/v1/catalog/products', { params }),
  getProduct: (id: string) => api.get(`/v1/catalog/products/${id}`),
}

// Orders
export const orderApi = {
  getCart: () => api.get('/v1/orders/cart'),
  saveCart: (items: any[]) => api.post('/v1/orders/cart', items),
  clearCart: () => api.delete('/v1/orders/cart'),
  placeOrder: (data: any) => api.post('/v1/orders', data),
  getOrders: () => api.get('/v1/orders'),
  getOrder: (id: string) => api.get(`/v1/orders/${id}`),
  cancelOrder: (id: string) => api.post(`/v1/orders/${id}/cancel`),
  getOrderStatus: (id: string) => api.get(`/v1/orders/${id}/status`),
}

// Customer
export const customerApi = {
  getProfile: () => api.get('/v1/customers/profile'),
  updateProfile: (data: any) => api.put('/v1/customers/profile', data),
  getAddresses: () => api.get('/v1/customers/addresses'),
  addAddress: (data: any) => api.post('/v1/customers/addresses', data),
  updateAddress: (id: string, data: any) => api.put(`/v1/customers/addresses/${id}`, data),
  deleteAddress: (id: string) => api.delete(`/v1/customers/addresses/${id}`),
  getFavorites: () => api.get('/v1/customers/favorites'),
  addFavorite: (productId: string, productName: string) =>
    api.post('/v1/customers/favorites', { productId, productName }),
  removeFavorite: (productId: string) => api.delete(`/v1/customers/favorites/${productId}`),
  getLoyalty: () => api.get('/v1/customers/loyalty'),
}

// Payments
export const paymentApi = {
  pay: (orderId: string) => api.post(`/v1/payments/${orderId}/pay`),
  getPayment: (orderId: string) => api.get(`/v1/payments/${orderId}`),
  refund: (orderId: string, reason?: string) =>
    api.post(`/v1/payments/${orderId}/refund`, null, { params: { reason } }),
}

// Notifications
export const notificationApi = {
  getNotifications: () => api.get('/v1/notifications'),
  getUnreadCount: () => api.get('/v1/notifications/unread-count'),
  markAsRead: (id: string) => api.put(`/v1/notifications/${id}/read`),
  markAllAsRead: () => api.put('/v1/notifications/read-all'),
}

export default api
