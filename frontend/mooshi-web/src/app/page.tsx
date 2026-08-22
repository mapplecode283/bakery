'use client'

import Link from 'next/link'
import { useQuery } from '@tanstack/react-query'
import { catalogApi } from '@/services/api'
import type { Product } from '@/types'
import { Coffee, Star, ArrowRight } from 'lucide-react'

export default function HomePage() {
  const { data: productsData } = useQuery({
    queryKey: ['popular-products'],
    queryFn: () => catalogApi.getProducts({ popular: true }),
  })

  const products: Product[] = productsData?.data?.data ?? []

  return (
    <div>
      {/* Hero */}
      <section className="relative bg-gradient-to-br from-coffee-900 via-coffee-800 to-mooshi-900 text-white py-20 sm:py-32">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 text-center">
          <h1 className="font-display text-4xl sm:text-6xl font-bold mb-6 leading-tight">
            Craft Your Perfect<br />
            <span className="text-mooshi-300">Coffee Moment</span>
          </h1>
          <p className="text-coffee-300 text-lg mb-8 max-w-xl mx-auto">
            Premium artisanal coffee, made just the way you like it. Order online for pickup or delivery.
          </p>
          <div className="flex gap-4 justify-center">
            <Link href="/menu" className="btn-primary bg-mooshi-500 hover:bg-mooshi-600 text-lg py-4 px-8">
              Browse Menu
            </Link>
            <Link href="/auth/register" className="btn-secondary text-lg py-4 px-8 bg-white/10 hover:bg-white/20 text-white border border-white/20">
              Sign Up Free
            </Link>
          </div>
        </div>
      </section>

      {/* Popular Products */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 py-16">
        <div className="flex items-center justify-between mb-8">
          <h2 className="font-display text-3xl font-bold text-coffee-900">Popular Now</h2>
          <Link href="/menu" className="flex items-center gap-2 text-mooshi-600 hover:text-mooshi-700 font-medium">
            View All <ArrowRight className="w-4 h-4" />
          </Link>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {products.map((product) => (
            <Link key={product.id} href={`/menu/${product.id}`} className="card hover:shadow-md transition-shadow group">
              <div className="w-full h-48 bg-gradient-to-br from-coffee-200 to-coffee-100 rounded-lg mb-4 flex items-center justify-center">
                <Coffee className="w-16 h-16 text-coffee-400 group-hover:text-mooshi-500 transition-colors" />
              </div>
              <h3 className="font-semibold text-coffee-900 group-hover:text-mooshi-600 transition-colors">
                {product.name}
              </h3>
              <p className="text-sm text-coffee-500 mt-1 line-clamp-2">{product.description}</p>
              <div className="flex items-center justify-between mt-3">
                <span className="text-lg font-bold text-coffee-900">
                  RM {product.basePrice.toFixed(2)}
                </span>
                {product.popular && (
                  <span className="flex items-center gap-1 text-amber-500 text-sm">
                    <Star className="w-4 h-4 fill-current" /> Popular
                  </span>
                )}
              </div>
            </Link>
          ))}
        </div>
      </section>

      {/* Features */}
      <section className="bg-white py-16 border-y border-coffee-100">
        <div className="max-w-7xl mx-auto px-4 sm:px-6">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8 text-center">
            {[
              { title: 'Premium Beans', desc: 'Sourced from the finest coffee farms worldwide' },
              { title: 'Custom Brew', desc: 'Personalize every drink to your taste' },
              { title: 'Fast Service', desc: 'Pickup in 10 minutes or delivered to your door' },
            ].map((f) => (
              <div key={f.title}>
                <h3 className="font-display text-xl font-semibold text-coffee-900 mb-2">{f.title}</h3>
                <p className="text-coffee-500">{f.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>
    </div>
  )
}
