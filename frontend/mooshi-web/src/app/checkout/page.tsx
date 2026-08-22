'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import { useCartStore } from '@/store/cart'
import { orderApi } from '@/services/api'
import { Coffee } from 'lucide-react'

export default function CheckoutPage() {
  const { items, getTotal, clearCart } = useCartStore()
  const router = useRouter()
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [deliveryType, setDeliveryType] = useState<'PICKUP' | 'DELIVERY'>('PICKUP')
  const [notes, setNotes] = useState('')

  const { subtotal } = getTotal()
  const tax = subtotal * 0.06
  const total = subtotal + tax

  if (items.length === 0) {
    router.push('/cart')
    return null
  }

  const handlePlaceOrder = async () => {
    setLoading(true)
    setError('')
    try {
      await orderApi.placeOrder({
        deliveryType,
        notes,
        items,
        subtotal,
        tax,
        deliveryFee: 0,
        totalAmount: total,
      })
      clearCart()
      router.push('/orders?placed=true')
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to place order')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 py-8">
      <h1 className="font-display text-3xl font-bold text-coffee-900 mb-6">Checkout</h1>

      {error && <div className="bg-red-50 text-red-600 p-3 rounded-lg mb-4 text-sm">{error}</div>}

      {/* Order Summary */}
      <div className="card mb-6">
        <h2 className="font-semibold text-coffee-900 mb-3">Order Summary</h2>
        <div className="space-y-2 text-sm">
          {items.map(item => (
            <div key={`${item.productId}-${item.size}`} className="flex justify-between">
              <span>{item.quantity}x {item.productName} ({item.size})</span>
              <span>RM {(item.unitPrice * item.quantity).toFixed(2)}</span>
            </div>
          ))}
          <div className="border-t border-coffee-100 pt-2 mt-2 space-y-1">
            <div className="flex justify-between text-coffee-500">
              <span>Subtotal</span><span>RM {subtotal.toFixed(2)}</span>
            </div>
            <div className="flex justify-between text-coffee-500">
              <span>Tax (6%)</span><span>RM {tax.toFixed(2)}</span>
            </div>
            <div className="flex justify-between font-bold text-lg pt-1 border-t">
              <span>Total</span><span>RM {total.toFixed(2)}</span>
            </div>
          </div>
        </div>
      </div>

      {/* Delivery Method */}
      <div className="card mb-6">
        <h2 className="font-semibold text-coffee-900 mb-3">Delivery Method</h2>
        <div className="grid grid-cols-2 gap-3">
          <button
            onClick={() => setDeliveryType('PICKUP')}
            className={`py-3 px-4 rounded-lg border text-sm font-medium transition-colors ${
              deliveryType === 'PICKUP'
                ? 'border-mooshi-600 bg-mooshi-50 text-mooshi-700'
                : 'border-coffee-200 text-coffee-600 hover:border-mooshi-400'
            }`}
          >
            Pickup
          </button>
          <button
            onClick={() => setDeliveryType('DELIVERY')}
            className={`py-3 px-4 rounded-lg border text-sm font-medium transition-colors ${
              deliveryType === 'DELIVERY'
                ? 'border-mooshi-600 bg-mooshi-50 text-mooshi-700'
                : 'border-coffee-200 text-coffee-600 hover:border-mooshi-400'
            }`}
          >
            Delivery
          </button>
        </div>
      </div>

      {/* Notes */}
      <div className="card mb-6">
        <label className="block text-sm font-medium text-coffee-700 mb-2">Special Notes</label>
        <textarea className="input-field" rows={3} value={notes}
          onChange={e => setNotes(e.target.value)} placeholder="Any special requests..." />
      </div>

      <button onClick={handlePlaceOrder} disabled={loading} className="btn-primary w-full flex items-center justify-center gap-2">
        <Coffee className="w-5 h-5" />
        {loading ? 'Placing Order...' : `Place Order — RM ${total.toFixed(2)}`}
      </button>
    </div>
  )
}
