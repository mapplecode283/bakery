'use client'

import { useState } from 'react'
import { useParams, useRouter } from 'next/navigation'
import { useQuery } from '@tanstack/react-query'
import { catalogApi } from '@/services/api'
import { useCartStore } from '@/store/cart'
import { useAuthStore } from '@/store/auth'
import type { Product, CartItem } from '@/types'
import { Coffee, Minus, Plus, ShoppingBag } from 'lucide-react'

export default function ProductDetailPage() {
  const { id } = useParams<{ id: string }>()
  const router = useRouter()
  const { isAuthenticated } = useAuthStore()
  const { addItem } = useCartStore()

  const [selectedSize, setSelectedSize] = useState<string>('')
  const [selectedOptions, setSelectedOptions] = useState<Set<string>>(new Set())
  const [quantity, setQuantity] = useState(1)
  const [added, setAdded] = useState(false)

  const { data, isLoading } = useQuery({
    queryKey: ['product', id],
    queryFn: () => catalogApi.getProduct(id),
    enabled: !!id,
  })

  const product: Product | null = data?.data?.data ?? null

  if (isLoading) return <div className="text-center py-20 text-coffee-500">Loading...</div>
  if (!product) return <div className="text-center py-20 text-coffee-500">Product not found</div>

  const calculatePrice = () => {
    let price = product.basePrice
    if (selectedSize) {
      const size = product.sizes.find(s => s.id === selectedSize)
      if (size) price *= size.priceMultiplier
    }
    selectedOptions.forEach(optId => {
      const opt = product.options.find(o => o.id === optId)
      if (opt) price += opt.priceAdjustment
    })
    return price
  }

  const handleAddToCart = () => {
    if (!isAuthenticated) {
      router.push('/auth/login')
      return
    }
    const unitPrice = calculatePrice()
    const sizeName = product.sizes.find(s => s.id === selectedSize)?.name || 'Regular'
    const options = Array.from(selectedOptions).map(id => {
      const opt = product.options.find(o => o.id === id)!
      return { optionId: opt.id, name: opt.name, priceAdjustment: opt.priceAdjustment }
    })

    addItem({
      productId: product.id,
      productName: product.name,
      size: sizeName,
      quantity,
      unitPrice,
      options,
    })
    setAdded(true)
    setTimeout(() => setAdded(false), 2000)
  }

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 py-8">
      <button onClick={() => router.back()} className="text-coffee-500 hover:text-mooshi-600 mb-6 text-sm">
        &larr; Back to Menu
      </button>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div className="aspect-square bg-gradient-to-br from-coffee-200 to-coffee-100 rounded-2xl flex items-center justify-center">
          <Coffee className="w-32 h-32 text-coffee-400" />
        </div>

        <div>
          <h1 className="font-display text-3xl font-bold text-coffee-900">{product.name}</h1>
          <p className="text-coffee-500 mt-2">{product.description}</p>
          <p className="text-2xl font-bold text-coffee-900 mt-4">RM {calculatePrice().toFixed(2)}</p>

          {/* Size Selection */}
          <div className="mt-6">
            <label className="block text-sm font-medium text-coffee-700 mb-2">Size</label>
            <div className="grid grid-cols-3 gap-2">
              {product.sizes.map(size => (
                <button
                  key={size.id}
                  onClick={() => setSelectedSize(size.id)}
                  className={`py-2 px-3 rounded-lg border text-sm font-medium transition-colors ${
                    selectedSize === size.id
                      ? 'border-mooshi-600 bg-mooshi-50 text-mooshi-700'
                      : 'border-coffee-200 text-coffee-600 hover:border-mooshi-400'
                  }`}
                >
                  {size.name}
                  <span className="block text-xs text-coffee-400">{(size.priceMultiplier * 100 - 100).toFixed(0)}%</span>
                </button>
              ))}
            </div>
          </div>

          {/* Options */}
          {product.options.length > 0 && (
            <div className="mt-4">
              <label className="block text-sm font-medium text-coffee-700 mb-2">Add-ons</label>
              <div className="space-y-2">
                {product.options.map(option => (
                  <label key={option.id} className="flex items-center gap-2 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={selectedOptions.has(option.id)}
                      onChange={e => {
                        const next = new Set(selectedOptions)
                        e.target.checked ? next.add(option.id) : next.delete(option.id)
                        setSelectedOptions(next)
                      }}
                      className="rounded border-coffee-300 text-mooshi-600 focus:ring-mooshi-500"
                    />
                    <span className="text-sm text-coffee-700">{option.name}</span>
                    <span className="text-sm text-coffee-400 ml-auto">
                      +RM {option.priceAdjustment.toFixed(2)}
                    </span>
                  </label>
                ))}
              </div>
            </div>
          )}

          {/* Quantity */}
          <div className="mt-6">
            <label className="block text-sm font-medium text-coffee-700 mb-2">Quantity</label>
            <div className="flex items-center gap-3">
              <button
                onClick={() => setQuantity(Math.max(1, quantity - 1))}
                className="p-2 border border-coffee-200 rounded-lg hover:bg-coffee-100"
              >
                <Minus className="w-4 h-4" />
              </button>
              <span className="w-8 text-center font-medium">{quantity}</span>
              <button
                onClick={() => setQuantity(quantity + 1)}
                className="p-2 border border-coffee-200 rounded-lg hover:bg-coffee-100"
              >
                <Plus className="w-4 h-4" />
              </button>
            </div>
          </div>

          {/* Add to Cart */}
          <button onClick={handleAddToCart} className="btn-primary w-full mt-6 flex items-center justify-center gap-2">
            <ShoppingBag className="w-5 h-5" />
            {added ? 'Added!' : 'Add to Cart'} — RM {(calculatePrice() * quantity).toFixed(2)}
          </button>
        </div>
      </div>
    </div>
  )
}
