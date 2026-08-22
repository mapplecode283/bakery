'use client'

import { useQuery, useQueryClient } from '@tanstack/react-query'
import { notificationApi } from '@/services/api'
import { Bell, ArrowLeft } from 'lucide-react'
import Link from 'next/link'
import type { Notification } from '@/types'

export default function NotificationsPage() {
  const queryClient = useQueryClient()

  const { data } = useQuery({
    queryKey: ['notifications'],
    queryFn: notificationApi.getNotifications,
  })

  const notifications: Notification[] = data?.data?.data ?? []

  const handleMarkAllRead = async () => {
    await notificationApi.markAllAsRead()
    queryClient.invalidateQueries({ queryKey: ['notifications'] })
    queryClient.invalidateQueries({ queryKey: ['unread-count'] })
  }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 py-8">
      <Link href="/profile" className="flex items-center gap-2 text-coffee-500 hover:text-mooshi-600 mb-6 text-sm">
        <ArrowLeft className="w-4 h-4" /> Back to Profile
      </Link>
      <div className="flex items-center justify-between mb-6">
        <h1 className="font-display text-3xl font-bold text-coffee-900">Notifications</h1>
        <button onClick={handleMarkAllRead} className="text-sm text-mooshi-600 hover:text-mooshi-700 font-medium">
          Mark All Read
        </button>
      </div>

      {notifications.length === 0 ? (
        <div className="text-center py-12 text-coffee-500">
          <Bell className="w-12 h-12 mx-auto mb-3 text-coffee-300" />
          <p>No notifications yet</p>
        </div>
      ) : (
        <div className="space-y-2">
          {notifications.map(notif => (
            <div key={notif.id} className={`card ${!notif.read ? 'border-l-4 border-l-mooshi-500 bg-mooshi-50' : ''}`}>
              <div className="flex items-start justify-between gap-3">
                <div>
                  <h3 className={`font-medium text-coffee-900 ${!notif.read ? 'font-semibold' : ''}`}>
                    {notif.title}
                  </h3>
                  <p className="text-sm text-coffee-500 mt-1">{notif.body}</p>
                  <div className="flex items-center gap-2 mt-2 text-xs text-coffee-400">
                    <span>{notif.type}</span>
                    <span>•</span>
                    <span>{new Date(notif.createdAt).toLocaleDateString('en-MY')}</span>
                  </div>
                </div>
                {!notif.read && (
                  <span className="w-2 h-2 bg-mooshi-500 rounded-full flex-shrink-0 mt-2" />
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
