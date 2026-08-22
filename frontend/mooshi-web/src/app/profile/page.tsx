'use client'

import Link from 'next/link'
import { useQuery } from '@tanstack/react-query'
import { customerApi, notificationApi } from '@/services/api'
import { useAuthStore } from '@/store/auth'
import type { CustomerAddress } from '@/types'
import { User, MapPin, Heart, Star, Bell, ChevronRight, LogOut } from 'lucide-react'

export default function ProfilePage() {
  const { logout, isAuthenticated } = useAuthStore()

  const { data: profileData } = useQuery({
    queryKey: ['profile'],
    queryFn: customerApi.getProfile,
    enabled: isAuthenticated,
  })

  const { data: unreadData } = useQuery({
    queryKey: ['unread-count'],
    queryFn: notificationApi.getUnreadCount,
    enabled: isAuthenticated,
  })

  const profile = profileData?.data?.data
  const unreadCount = unreadData?.data?.data?.unreadCount ?? 0

  if (!isAuthenticated) {
    return (
      <div className="max-w-2xl mx-auto px-4 py-20 text-center">
        <User className="w-16 h-16 text-coffee-300 mx-auto mb-4" />
        <h1 className="text-2xl font-bold text-coffee-900 mb-4">Please sign in</h1>
        <Link href="/auth/login" className="btn-primary">Sign In</Link>
      </div>
    )
  }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 py-8">
      <h1 className="font-display text-3xl font-bold text-coffee-900 mb-6">My Profile</h1>

      {profile && (
        <div className="card mb-6 text-center">
          <div className="w-20 h-20 bg-coffee-200 rounded-full mx-auto mb-3 flex items-center justify-center">
            <User className="w-10 h-10 text-coffee-500" />
          </div>
          <h2 className="text-xl font-semibold text-coffee-900">
            {profile.firstName} {profile.lastName}
          </h2>
          <p className="text-coffee-500">{profile.email}</p>
          {profile.phone && <p className="text-coffee-500">{profile.phone}</p>}
          <p className="text-mooshi-600 font-medium mt-2">{profile.loyaltyPoints} Loyalty Points</p>
        </div>
      )}

      <div className="space-y-2">
        {[
          { icon: MapPin, label: 'Addresses', href: '/profile/addresses' },
          { icon: Heart, label: 'Favorites', href: '/profile/favorites' },
          { icon: Star, label: 'Loyalty Points', href: '/profile/loyalty' },
          { icon: Bell, label: 'Notifications', href: '/profile/notifications', badge: unreadCount > 0 ? unreadCount : null },
        ].map(item => (
          <Link key={item.href} href={item.href}
            className="card flex items-center justify-between hover:shadow-md transition-shadow">
            <div className="flex items-center gap-3">
              <item.icon className="w-5 h-5 text-coffee-500" />
              <span className="font-medium text-coffee-900">{item.label}</span>
            </div>
            <div className="flex items-center gap-2">
              {item.badge && (
                <span className="bg-mooshi-600 text-white text-xs rounded-full w-6 h-6 flex items-center justify-center">
                  {item.badge}
                </span>
              )}
              <ChevronRight className="w-5 h-5 text-coffee-400" />
            </div>
          </Link>
        ))}
      </div>

      <button onClick={logout}
        className="card mt-4 flex items-center gap-3 text-red-600 hover:shadow-md transition-shadow w-full">
        <LogOut className="w-5 h-5" />
        <span className="font-medium">Sign Out</span>
      </button>
    </div>
  )
}
