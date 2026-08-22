'use client'

import Link from 'next/link'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { customerApi } from '@/services/api'
import { Heart, ArrowLeft } from 'lucide-react'

export default function FavoritesPage() {
  const queryClient = useQueryClient()

  const { data } = useQuery({
    queryKey: ['favorites'],
    queryFn: customerApi.getFavorites,
  })

  const favorites = data?.data?.data ?? []

  const handleRemove = async (productId: string) => {
    await customerApi.removeFavorite(productId)
    queryClient.invalidateQueries({ queryKey: ['favorites'] })
  }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 py-8">
      <Link href="/profile" className="flex items-center gap-2 text-coffee-500 hover:text-mooshi-600 mb-6 text-sm">
        <ArrowLeft className="w-4 h-4" /> Back to Profile
      </Link>
      <h1 className="font-display text-3xl font-bold text-coffee-900 mb-6">Favorites</h1>

      {favorites.length === 0 ? (
        <div className="text-center py-12 text-coffee-500">
          <Heart className="w-12 h-12 mx-auto mb-3 text-coffee-300" />
          <p>No favorites yet. Browse the menu to save your favorites!</p>
        </div>
      ) : (
        <div className="space-y-3">
          {favorites.map((fav: any) => (
            <div key={fav.id} className="card flex items-center justify-between">
              <Link href={`/menu/${fav.productId}`} className="flex items-center gap-3 flex-1">
                <Heart className="w-5 h-5 text-red-400 fill-red-400" />
                <span className="font-medium text-coffee-900">{fav.productName}</span>
              </Link>
              <button onClick={() => handleRemove(fav.productId)} className="text-red-400 hover:text-red-600 text-sm">
                Remove
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
