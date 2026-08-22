'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import Link from 'next/link'
import { authApi } from '@/services/api'
import { useAuthStore } from '@/store/auth'
import { Coffee } from 'lucide-react'

export default function LoginPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const router = useRouter()
  const { setTokens } = useAuthStore()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const res = await authApi.login({ email, password })
      const { accessToken, refreshToken } = res.data.data
      setTokens(accessToken, refreshToken)
      router.push('/')
    } catch (err: any) {
      setError(err.response?.data?.message || 'Login failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-[80vh] flex items-center justify-center px-4">
      <div className="card w-full max-w-md">
        <div className="text-center mb-8">
          <Coffee className="w-12 h-12 text-mooshi-600 mx-auto mb-3" />
          <h1 className="font-display text-2xl font-bold text-coffee-900">Welcome Back</h1>
          <p className="text-coffee-500 mt-1">Sign in to your Mooshi account</p>
        </div>

        {error && (
          <div className="bg-red-50 text-red-600 p-3 rounded-lg mb-4 text-sm">{error}</div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-coffee-700 mb-1">Email</label>
            <input type="email" className="input-field" value={email} onChange={e => setEmail(e.target.value)} required />
          </div>
          <div>
            <label className="block text-sm font-medium text-coffee-700 mb-1">Password</label>
            <input type="password" className="input-field" value={password} onChange={e => setPassword(e.target.value)} required />
          </div>
          <button type="submit" className="btn-primary w-full" disabled={loading}>
            {loading ? 'Signing in...' : 'Sign In'}
          </button>
        </form>

        <div className="text-center mt-6 text-sm text-coffee-500">
          <Link href="/auth/forgot-password" className="hover:text-mooshi-600">Forgot password?</Link>
        </div>
        <div className="text-center mt-3 text-sm text-coffee-500">
          Don&apos;t have an account?{' '}
          <Link href="/auth/register" className="text-mooshi-600 hover:text-mooshi-700 font-medium">Sign Up</Link>
        </div>
      </div>
    </div>
  )
}
