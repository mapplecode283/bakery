'use client'

import Link from 'next/link'
import { useQuery } from '@tanstack/react-query'
import { orderApi } from '@/services/api'
import { useAuthStore } from '@/store/auth'
import type { Order } from '@/types'
import { Package } from 'lucide-react'

const statusColors: Record<string, string> = {
  CREATED: 'bg-blue-100 text-blue-700',
  PAYMENT_PENDING: 'bg-yellow-100 text-yellow-700',
  PAID: 'bg-green-100 text-green-700',
  PREPARING: 'bg-purple-100 text-purple-700',
  READY_FOR_PICKUP: 'bg-teal-100 text-teal-700',
  OUT_FOR_DELIVERY: 'bg-indigo-100 text-indigo-700',
  DELIVERED: 'bg-green-100 text-green-700',
  COMPLETED: 'bg-green-200 text-green-800',
  CANCELLED: 'bg-red-100 text-red-700',
}

export default function OrdersPage() {
  const { isAuthenticated } = useAuthStore()

  const { data, isLoading } = useQuery({
    queryKey: ['orders'],
    queryFn: orderApi.getOrders,
    enabled: isAuthenticated,
  })

  const orders: Order[] = data?.data?.data ?? []

  if (!isAuthenticated) {
    return (
      <div className="max-w-2xl mx-auto px-4 py-20 text-center">
        <Package className="w-16 h-16 text-coffee-300 mx-auto mb-4" />
        <h1 className="text-2xl font-bold text-coffee-900 mb-2">My Orders</h1>
        <p className="text-coffee-500 mb-4">Sign in to view your orders</p>
        <Link href="/auth/login" className="btn-primary">Sign In</Link>
      </div>
    )
  }

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 py-8">
      <h1 className="font-display text-3xl font-bold text-coffee-900 mb-6">My Orders</h1>

      {isLoading ? (
        <p className="text-center text-coffee-500 py-8">Loading orders...</p>
      ) : orders.length === 0 ? (
        <div className="text-center py-12">
          <Package className="w-16 h-16 text-coffee-300 mx-auto mb-4" />
          <h2 className="text-xl font-semibold text-coffee-900 mb-2">No orders yet</h2>
          <p className="text-coffee-500 mb-4">Start exploring our menu</p>
          <Link href="/menu" className="btn-primary">Browse Menu</Link>
        </div>
      ) : (
        <div className="space-y-4">
          {orders.map(order => (
            <Link key={order.id} href={`/orders/${order.id}`} className="card block hover:shadow-md transition-shadow">
              <div className="flex items-center justify-between mb-3">
                <div>
                  <p className="text-sm text-coffee-500">Order #{order.id.substring(0, 8)}</p>
                  <p className="text-xs text-coffee-400">
                    {new Date(order.createdAt).toLocaleDateString('en-MY', {
                      day: 'numeric', month: 'short', year: 'numeric',
                      hour: '2-digit', minute: '2-digit',
                    })}
                  </p>
                </div>
                <span className={`px-3 py-1 rounded-full text-xs font-medium ${statusColors[order.status] || 'bg-gray-100'}`}>
                  {order.status.replace(/_/g, ' ')}
                </span>
              </div>
              <div className="text-sm text-coffee-500">
                {order.itemCount} {order.itemCount === 1 ? 'item' : 'items'}
              </div>
              <div className="flex justify-between items-center mt-3 pt-3 border-t border-coffee-100">
                <span className="text-xs text-coffee-500">
                  {order.deliveryType === 'PICKUP' ? 'Pickup' : 'Delivery'}
                </span>
                <span className="font-semibold text-coffee-900">RM {order.totalAmount.toFixed(2)}</span>
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  )
}
