'use client'

import Link from 'next/link'
import { useAuthStore } from '@/store/auth'
import { useCartStore } from '@/store/cart'
import { Coffee, ShoppingBag, User, Menu, X } from 'lucide-react'
import { useState } from 'react'

export function Header() {
  const { isAuthenticated, logout } = useAuthStore()
  const { getTotal } = useCartStore()
  const [menuOpen, setMenuOpen] = useState(false)
  const { count } = getTotal()

  return (
    <header className="bg-white border-b border-coffee-200 sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6">
        <div className="flex items-center justify-between h-16">
          <Link href="/" className="flex items-center gap-2">
            <Coffee className="w-8 h-8 text-mooshi-600" />
            <span className="font-display text-2xl font-bold text-coffee-900">Mooshi</span>
          </Link>

          <nav className="hidden md:flex items-center gap-8">
            <Link href="/menu" className="text-coffee-700 hover:text-mooshi-600 font-medium transition-colors">
              Menu
            </Link>
            {isAuthenticated && (
              <Link href="/orders" className="text-coffee-700 hover:text-mooshi-600 font-medium transition-colors">
                Orders
              </Link>
            )}
          </nav>

          <div className="flex items-center gap-4">
            {isAuthenticated ? (
              <>
                <Link href="/cart" className="relative p-2 text-coffee-700 hover:text-mooshi-600">
                  <ShoppingBag className="w-6 h-6" />
                  {count > 0 && (
                    <span className="absolute -top-1 -right-1 bg-mooshi-600 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
                      {count}
                    </span>
                  )}
                </Link>
                <Link href="/profile" className="p-2 text-coffee-700 hover:text-mooshi-600">
                  <User className="w-6 h-6" />
                </Link>
                <button onClick={logout} className="text-sm text-coffee-500 hover:text-coffee-700">
                  Logout
                </button>
              </>
            ) : (
              <Link href="/auth/login" className="btn-primary text-sm py-2 px-4">
                Sign In
              </Link>
            )}
            <button className="md:hidden p-2" onClick={() => setMenuOpen(!menuOpen)}>
              {menuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
          </div>
        </div>

        {menuOpen && (
          <div className="md:hidden py-4 border-t border-coffee-100">
            <Link href="/menu" className="block py-2 text-coffee-700">Menu</Link>
            {isAuthenticated && (
              <Link href="/orders" className="block py-2 text-coffee-700">Orders</Link>
            )}
          </div>
        )}
      </div>
    </header>
  )
}
