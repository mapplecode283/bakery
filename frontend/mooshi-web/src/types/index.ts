export interface User {
  id: string
  email: string
  firstName: string
  lastName: string
  enabled: boolean
  createdAt: string
}

export interface TokenResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
}

export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
  timestamp: string
}

export interface Category {
  id: string
  name: string
  description: string
  imageUrl?: string
  sortOrder: number
}

export interface ProductOption {
  id: string
  name: string
  priceAdjustment: number
}

export interface ProductSize {
  id: string
  name: string
  priceMultiplier: number
}

export interface Product {
  id: string
  name: string
  description: string
  imageUrl?: string
  basePrice: number
  categoryId: string
  popular: boolean
  options: ProductOption[]
  sizes: ProductSize[]
}

export interface CartItem {
  productId: string
  productName: string
  size: string
  quantity: number
  unitPrice: number
  options: { optionId: string; name: string; priceAdjustment: number }[]
}

export type OrderStatus =
  | 'CREATED' | 'PAYMENT_PENDING' | 'PAID' | 'PREPARING'
  | 'READY_FOR_PICKUP' | 'OUT_FOR_DELIVERY' | 'DELIVERED'
  | 'COMPLETED' | 'CANCELLED'

export interface OrderItem {
  id: string
  productId: string
  productName: string
  size: string
  quantity: number
  unitPrice: number
  subtotal: number
  optionsJson: string
}

export interface Order {
  id: string
  customerId: string
  status: OrderStatus
  subtotal: number
  tax: number
  deliveryFee: number
  totalAmount: number
  deliveryType: 'PICKUP' | 'DELIVERY'
  notes?: string
  items: OrderItem[]
  statusHistory: { id: string; status: OrderStatus; note: string; createdAt: string }[]
  createdAt: string
  updatedAt: string
}

export interface CustomerAddress {
  id: string
  label: string
  street: string
  city: string
  state: string
  zipCode: string
  country: string
  isDefault: boolean
}

export interface Notification {
  id: string
  userId: string
  type: 'EMAIL' | 'SMS' | 'PUSH'
  title: string
  body: string
  read: boolean
  createdAt: string
}
