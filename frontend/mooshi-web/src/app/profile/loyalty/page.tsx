'use client'

import Link from 'next/link'
import { useQuery } from '@tanstack/react-query'
import { customerApi } from '@/services/api'
import { Star, ArrowLeft, Gift } from 'lucide-react'

export default function LoyaltyPage() {
  const { data: historyData } = useQuery({
    queryKey: ['loyalty'],
    queryFn: customerApi.getLoyalty,
  })

  const { data: profileData } = useQuery({
    queryKey: ['profile'],
    queryFn: customerApi.getProfile,
  })

  const history = historyData?.data?.data ?? []
  const totalPoints = profileData?.data?.data?.loyaltyPoints ?? 0

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 py-8">
      <Link href="/profile" className="flex items-center gap-2 text-coffee-500 hover:text-mooshi-600 mb-6 text-sm">
        <ArrowLeft className="w-4 h-4" /> Back to Profile
      </Link>
      <h1 className="font-display text-3xl font-bold text-coffee-900 mb-6">Loyalty Points</h1>

      <div className="card mb-6 text-center">
        <Gift className="w-12 h-12 text-mooshi-500 mx-auto mb-3" />
        <p className="text-4xl font-bold text-mooshi-600">{totalPoints}</p>
        <p className="text-coffee-500">Total Points Earned</p>
        <p className="text-xs text-coffee-400 mt-2">Earn 10 points for every RM1 spent</p>
      </div>

      {history.length === 0 ? (
        <div className="text-center py-8 text-coffee-500">
          <Star className="w-12 h-12 mx-auto mb-3 text-coffee-300" />
          <p>No point history yet. Place your first order to start earning!</p>
        </div>
      ) : (
        <div className="space-y-2">
          <h3 className="font-semibold text-coffee-900 mb-3">Points History</h3>
          {history.map((entry: any) => (
            <div key={entry.id} className="card flex items-center justify-between text-sm">
              <div>
                <p className="font-medium text-coffee-900">{entry.reason}</p>
                <p className="text-coffee-400 text-xs">
                  {new Date(entry.createdAt).toLocaleDateString('en-MY')}
                </p>
              </div>
              <span className="font-semibold text-mooshi-600">+{entry.points} pts</span>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
