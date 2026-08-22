import Link from 'next/link'
import { Coffee } from 'lucide-react'

export function Footer() {
  return (
    <footer className="bg-coffee-900 text-coffee-200 py-12 mt-auto">
      <div className="max-w-7xl mx-auto px-4 sm:px-6">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          <div>
            <div className="flex items-center gap-2 mb-4">
              <Coffee className="w-6 h-6 text-mooshi-400" />
              <span className="font-display text-xl font-bold text-white">Mooshi</span>
            </div>
            <p className="text-coffee-400 text-sm">
              Premium coffee crafted with passion. Order online and enjoy artisanal coffee at your doorstep.
            </p>
          </div>
          <div>
            <h4 className="text-white font-semibold mb-3">Menu</h4>
            <div className="space-y-2 text-sm">
              <Link href="/menu?category=cat-hot-coffee" className="block hover:text-mooshi-400 transition-colors">Hot Coffee</Link>
              <Link href="/menu?category=cat-iced-coffee" className="block hover:text-mooshi-400 transition-colors">Iced Coffee</Link>
              <Link href="/menu?category=cat-tea" className="block hover:text-mooshi-400 transition-colors">Tea</Link>
              <Link href="/menu?category=cat-blended" className="block hover:text-mooshi-400 transition-colors">Blended</Link>
            </div>
          </div>
          <div>
            <h4 className="text-white font-semibold mb-3">Account</h4>
            <div className="space-y-2 text-sm">
              <Link href="/profile" className="block hover:text-mooshi-400 transition-colors">Profile</Link>
              <Link href="/orders" className="block hover:text-mooshi-400 transition-colors">Orders</Link>
              <Link href="/profile/favorites" className="block hover:text-mooshi-400 transition-colors">Favorites</Link>
            </div>
          </div>
          <div>
            <h4 className="text-white font-semibold mb-3">Contact</h4>
            <div className="space-y-2 text-sm text-coffee-400">
              <p>hello@mooshi.coffee</p>
              <p>+60 3-1234 5678</p>
              <p>Kuala Lumpur, Malaysia</p>
            </div>
          </div>
        </div>
        <div className="border-t border-coffee-800 mt-8 pt-8 text-center text-sm text-coffee-500">
          &copy; {new Date().getFullYear()} Mooshi Coffee. All rights reserved.
        </div>
      </div>
    </footer>
  )
}
