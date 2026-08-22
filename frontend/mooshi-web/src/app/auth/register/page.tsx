'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import Link from 'next/link'
import { authApi } from '@/services/api'
import { Coffee } from 'lucide-react'

export default function RegisterPage() {
  const [form, setForm] = useState({ email: '', password: '', firstName: '', lastName: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const router = useRouter()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await authApi.register(form)
      router.push('/auth/login?registered=true')
    } catch (err: any) {
      setError(err.response?.data?.message || 'Registration failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-[80vh] flex items-center justify-center px-4">
      <div className="card w-full max-w-md">
        <div className="text-center mb-8">
          <Coffee className="w-12 h-12 text-mooshi-600 mx-auto mb-3" />
          <h1 className="font-display text-2xl font-bold text-coffee-900">Create Account</h1>
          <p className="text-coffee-500 mt-1">Join Mooshi and start ordering</p>
        </div>

        {error && (
          <div className="bg-red-50 text-red-600 p-3 rounded-lg mb-4 text-sm">{error}</div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-sm font-medium text-coffee-700 mb-1">First Name</label>
              <input type="text" className="input-field" value={form.firstName}
                onChange={e => setForm({ ...form, firstName: e.target.value })} required />
            </div>
            <div>
              <label className="block text-sm font-medium text-coffee-700 mb-1">Last Name</label>
              <input type="text" className="input-field" value={form.lastName}
                onChange={e => setForm({ ...form, lastName: e.target.value })} required />
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium text-coffee-700 mb-1">Email</label>
            <input type="email" className="input-field" value={form.email}
              onChange={e => setForm({ ...form, email: e.target.value })} required />
          </div>
          <div>
            <label className="block text-sm font-medium text-coffee-700 mb-1">Password</label>
            <input type="password" className="input-field" value={form.password}
              onChange={e => setForm({ ...form, password: e.target.value })} required minLength={8} />
          </div>
          <button type="submit" className="btn-primary w-full" disabled={loading}>
            {loading ? 'Creating...' : 'Create Account'}
          </button>
        </form>

        <div className="text-center mt-6 text-sm text-coffee-500">
          Already have an account?{' '}
          <Link href="/auth/login" className="text-mooshi-600 hover:text-mooshi-700 font-medium">Sign In</Link>
        </div>
      </div>
    </div>
  )
}
