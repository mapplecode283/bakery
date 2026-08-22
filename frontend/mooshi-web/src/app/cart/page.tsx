'use client'

import Link from 'next/link'
import { useCartStore } from '@/store/cart'
import { useAuthStore } from '@/store/auth'
import { Minus, Plus, ShoppingBag, Trash2 } from 'lucide-react'

export default function CartPage() {
  const { items, removeItem, updateQuantity, getTotal } = useCartStore()
  const { isAuthenticated } = useAuthStore()
  const { subtotal, count } = getTotal()

  const tax = subtotal * 0.06
  const total = subtotal + tax

  if (!isAuthenticated) {
    return (
      <div className="max-w-2xl mx-auto px-4 py-20 text-center">
        <ShoppingBag className="w-16 h-16 text-coffee-300 mx-auto mb-4" />
        <h1 className="text-2xl font-bold text-coffee-900 mb-2">Please sign in</h1>
        <p className="text-coffee-500 mb-4">Sign in to view your cart</p>
        <Link href="/auth/login" className="btn-primary">Sign In</Link>
      </div>
    )
  }

  if (items.length === 0) {
    return (
      <div className="max-w-2xl mx-auto px-4 py-20 text-center">
        <ShoppingBag className="w-16 h-16 text-coffee-300 mx-auto mb-4" />
        <h1 className="text-2xl font-bold text-coffee-900 mb-2">Your cart is empty</h1>
        <p className="text-coffee-500 mb-4">Start adding items to your cart</p>
        <Link href="/menu" className="btn-primary">Browse Menu</Link>
      </div>
    )
  }

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 py-8">
      <h1 className="font-display text-3xl font-bold text-coffee-900 mb-6">Your Cart ({count} items)</h1>

      <div className="space-y-4">
        {items.map(item => (
          <div key={`${item.productId}-${item.size}`} className="card flex items-center gap-4">
            <div className="flex-1">
              <h3 className="font-semibold text-coffee-900">{item.productName}</h3>
              <p className="text-sm text-coffee-500">{item.size}</p>
              {item.options.length > 0 && (
                <p className="text-xs text-coffee-400 mt-1">
                  {item.options.map(o => o.name).join(', ')}
                </p>
              )}
            </div>
            <div className="flex items-center gap-2">
              <button onClick={() => updateQuantity(item.productId, item.quantity - 1)}
                className="p-1 border rounded hover:bg-coffee-100">
                <Minus className="w-4 h-4" />
              </button>
              <span className="w-6 text-center text-sm">{item.quantity}</span>
              <button onClick={() => updateQuantity(item.productId, item.quantity + 1)}
                className="p-1 border rounded hover:bg-coffee-100">
                <Plus className="w-4 h-4" />
              </button>
            </div>
            <p className="font-semibold text-coffee-900 w-20 text-right">
              RM {(item.unitPrice * item.quantity).toFixed(2)}
            </p>
            <button onClick={() => removeItem(item.productId)} className="text-red-400 hover:text-red-600 p-1">
              <Trash2 className="w-4 h-4" />
            </button>
          </div>
        ))}
      </div>

      <div className="card mt-6">
        <div className="space-y-2 text-sm">
          <div className="flex justify-between">
            <span className="text-coffee-500">Subtotal</span>
            <span>RM {subtotal.toFixed(2)}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-coffee-500">Tax (6%)</span>
            <span>RM {tax.toFixed(2)}</span>
          </div>
          <div className="flex justify-between font-bold text-lg pt-2 border-t border-coffee-100">
            <span>Total</span>
            <span>RM {total.toFixed(2)}</span>
          </div>
        </div>
      </div>

      <Link href="/checkout" className="btn-primary w-full mt-6 text-center block">
        Proceed to Checkout
      </Link>
    </div>
  )
}
