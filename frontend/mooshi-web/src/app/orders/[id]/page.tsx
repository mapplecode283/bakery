'use client'

import { useParams, useRouter } from 'next/navigation'
import { useQuery } from '@tanstack/react-query'
import { orderApi, paymentApi } from '@/services/api'
import type { Order } from '@/types'
import { ArrowLeft, Clock } from 'lucide-react'

const STATUS_FLOW = [
  'CREATED', 'PAYMENT_PENDING', 'PAID',
  'PREPARING', 'READY_FOR_PICKUP', 'COMPLETED',
]

export default function OrderDetailPage() {
  const { id } = useParams<{ id: string }>()
  const router = useRouter()

  const { data, isLoading } = useQuery({
    queryKey: ['order', id],
    queryFn: () => orderApi.getOrder(id),
    enabled: !!id,
  })

  const order: Order | null = data?.data?.data ?? null

  if (isLoading) return <div className="text-center py-20 text-coffee-500">Loading order...</div>
  if (!order) return <div className="text-center py-20 text-coffee-500">Order not found</div>

  const currentStep = STATUS_FLOW.indexOf(order.status)

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 py-8">
      <button onClick={() => router.back()} className="flex items-center gap-2 text-coffee-500 hover:text-mooshi-600 mb-6 text-sm">
        <ArrowLeft className="w-4 h-4" /> Back to Orders
      </button>

      <div className="card mb-6">
        <div className="flex items-center justify-between mb-4">
          <div>
            <h1 className="font-display text-2xl font-bold text-coffee-900">
              Order #{order.id.substring(0, 8)}
            </h1>
            <p className="text-sm text-coffee-500">
              {new Date(order.createdAt).toLocaleDateString('en-MY', {
                day: 'numeric', month: 'long', year: 'numeric', hour: '2-digit', minute: '2-digit',
              })}
            </p>
          </div>
          <span className={`px-3 py-1 rounded-full text-sm font-medium ${
            order.status === 'CANCELLED' ? 'bg-red-100 text-red-700' :
            order.status === 'COMPLETED' ? 'bg-green-100 text-green-700' :
            'bg-blue-100 text-blue-700'
          }`}>
            {order.status.replace(/_/g, ' ')}
          </span>
        </div>

        {/* Status Tracker */}
        {order.status !== 'CANCELLED' && (
          <div className="flex items-center gap-2 mb-6">
            {STATUS_FLOW.map((status, i) => (
              <div key={status} className="flex items-center gap-2 flex-1">
                <div className={`w-6 h-6 rounded-full flex items-center justify-center text-xs ${
                  i <= currentStep ? 'bg-mooshi-600 text-white' : 'bg-coffee-200 text-coffee-500'
                }`}>
                  {i < currentStep ? '✓' : i + 1}
                </div>
                {i < STATUS_FLOW.length - 1 && (
                  <div className={`flex-1 h-1 rounded ${
                    i < currentStep ? 'bg-mooshi-600' : 'bg-coffee-200'
                  }`} />
                )}
              </div>
            ))}
          </div>
        )}

        {/* Items */}
        <div className="border-t border-coffee-100 pt-4">
          <h3 className="font-semibold text-coffee-900 mb-3">Items</h3>
          {order.items.map(item => (
            <div key={item.id} className="flex justify-between py-2 text-sm">
              <span>{item.quantity}x {item.productName} ({item.size})</span>
              <span className="font-medium">RM {item.subtotal.toFixed(2)}</span>
            </div>
          ))}
        </div>

        {/* Total */}
        <div className="border-t border-coffee-100 mt-4 pt-4 space-y-1 text-sm">
          <div className="flex justify-between text-coffee-500">
            <span>Subtotal</span><span>RM {order.subtotal.toFixed(2)}</span>
          </div>
          <div className="flex justify-between text-coffee-500">
            <span>Tax</span><span>RM {order.tax.toFixed(2)}</span>
          </div>
          <div className="flex justify-between font-bold text-lg pt-1 border-t">
            <span>Total</span><span>RM {order.totalAmount.toFixed(2)}</span>
          </div>
        </div>

        {/* Status History */}
        <div className="border-t border-coffee-100 mt-4 pt-4">
          <h3 className="font-semibold text-coffee-900 mb-2">Status History</h3>
          <div className="space-y-2">
            {order.statusHistory.map(h => (
              <div key={h.id} className="flex items-center gap-3 text-sm">
                <Clock className="w-4 h-4 text-coffee-400" />
                <span className="text-coffee-700">{h.status.replace(/_/g, ' ')}</span>
                {h.note && <span className="text-coffee-400">— {h.note}</span>}
                <span className="text-coffee-400 ml-auto text-xs">
                  {new Date(h.createdAt).toLocaleTimeString('en-MY', { hour: '2-digit', minute: '2-digit' })}
                </span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}
