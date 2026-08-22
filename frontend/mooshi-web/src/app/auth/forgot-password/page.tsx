'use client'

import { useState } from 'react'
import Link from 'next/link'
import { Coffee } from 'lucide-react'

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState('')
  const [sent, setSent] = useState(false)

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    setSent(true)
  }

  return (
    <div className="min-h-[80vh] flex items-center justify-center px-4">
      <div className="card w-full max-w-md">
        <div className="text-center mb-8">
          <Coffee className="w-12 h-12 text-mooshi-600 mx-auto mb-3" />
          <h1 className="font-display text-2xl font-bold text-coffee-900">Reset Password</h1>
          <p className="text-coffee-500 mt-1">We&apos;ll send you a reset link</p>
        </div>

        {sent ? (
          <div className="text-center">
            <div className="bg-green-50 text-green-600 p-4 rounded-lg mb-4 text-sm">
              If an account exists with that email, we have sent a password reset link.
            </div>
            <Link href="/auth/login" className="text-mooshi-600 hover:text-mooshi-700 font-medium text-sm">
              Back to Sign In
            </Link>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-coffee-700 mb-1">Email</label>
              <input type="email" className="input-field" value={email}
                onChange={e => setEmail(e.target.value)} required />
            </div>
            <button type="submit" className="btn-primary w-full">Send Reset Link</button>
            <div className="text-center text-sm text-coffee-500">
              <Link href="/auth/login" className="hover:text-mooshi-600">Back to Sign In</Link>
            </div>
          </form>
        )}
      </div>
    </div>
  )
}
