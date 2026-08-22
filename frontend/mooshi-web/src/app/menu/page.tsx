'use client'

import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import Link from 'next/link'
import { useSearchParams } from 'next/navigation'
import { catalogApi } from '@/services/api'
import type { Product, Category } from '@/types'
import { Coffee, Search, Star } from 'lucide-react'

export default function MenuPage() {
  const searchParams = useSearchParams()
  const [selectedCategory, setSelectedCategory] = useState(searchParams.get('category') || '')
  const [search, setSearch] = useState('')
  const [showPopular, setShowPopular] = useState(false)

  const { data: categoriesData } = useQuery({
    queryKey: ['categories'],
    queryFn: catalogApi.getCategories,
  })

  const { data: productsData, isLoading } = useQuery({
    queryKey: ['products', selectedCategory, search, showPopular],
    queryFn: () => catalogApi.getProducts({
      category: selectedCategory || undefined,
      search: search || undefined,
      popular: showPopular || undefined,
    }),
  })

  const categories: Category[] = categoriesData?.data?.data ?? []
  const products: Product[] = productsData?.data?.data ?? []

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 py-8">
      <h1 className="font-display text-3xl font-bold text-coffee-900 mb-6">Our Menu</h1>

      {/* Search & Filters */}
      <div className="flex flex-col sm:flex-row gap-4 mb-8">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-coffee-400" />
          <input
            type="text"
            placeholder="Search drinks..."
            className="input-field pl-10"
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
        </div>
        <div className="flex gap-2 overflow-x-auto">
          <button
            onClick={() => { setSelectedCategory(''); setShowPopular(false) }}
            className={`px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-colors ${
              !selectedCategory && !showPopular ? 'bg-mooshi-600 text-white' : 'bg-coffee-100 text-coffee-700 hover:bg-coffee-200'
            }`}
          >
            All
          </button>
          {categories.map(cat => (
            <button
              key={cat.id}
              onClick={() => { setSelectedCategory(cat.id); setShowPopular(false) }}
              className={`px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-colors ${
                selectedCategory === cat.id ? 'bg-mooshi-600 text-white' : 'bg-coffee-100 text-coffee-700 hover:bg-coffee-200'
              }`}
            >
              {cat.name}
            </button>
          ))}
          <button
            onClick={() => { setShowPopular(!showPopular); setSelectedCategory('') }}
            className={`px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-colors flex items-center gap-1 ${
              showPopular ? 'bg-mooshi-600 text-white' : 'bg-coffee-100 text-coffee-700 hover:bg-coffee-200'
            }`}
          >
            <Star className="w-4 h-4" /> Popular
          </button>
        </div>
      </div>

      {/* Products Grid */}
      {isLoading ? (
        <div className="text-center py-12 text-coffee-500">Loading menu...</div>
      ) : products.length === 0 ? (
        <div className="text-center py-12 text-coffee-500">
          <Coffee className="w-12 h-12 mx-auto mb-3 text-coffee-300" />
          <p>No drinks found. Try a different filter.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {products.map(product => (
            <Link key={product.id} href={`/menu/${product.id}`} className="card hover:shadow-md transition-shadow group">
              <div className="w-full h-40 bg-gradient-to-br from-coffee-200 to-coffee-100 rounded-lg mb-4 flex items-center justify-center">
                <Coffee className="w-12 h-12 text-coffee-400 group-hover:text-mooshi-500 transition-colors" />
              </div>
              <div className="flex items-start justify-between">
                <div>
                  <h3 className="font-semibold text-coffee-900 group-hover:text-mooshi-600">{product.name}</h3>
                  <p className="text-sm text-coffee-500 mt-1 line-clamp-2">{product.description}</p>
                </div>
                {product.popular && <Star className="w-4 h-4 fill-amber-400 text-amber-400 flex-shrink-0 mt-1" />}
              </div>
              <div className="mt-3">
                <span className="text-lg font-bold text-coffee-900">RM {product.basePrice.toFixed(2)}</span>
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  )
}
