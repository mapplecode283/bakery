'use client'

import { useState } from 'react'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { customerApi } from '@/services/api'
import type { CustomerAddress } from '@/types'
import { MapPin, Plus, ArrowLeft } from 'lucide-react'
import Link from 'next/link'

export default function AddressesPage() {
  const queryClient = useQueryClient()
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState({ label: '', street: '', city: '', state: '', zipCode: '', isDefault: false })

  const { data, isLoading } = useQuery({
    queryKey: ['addresses'],
    queryFn: customerApi.getAddresses,
  })

  const addresses: CustomerAddress[] = data?.data?.data ?? []

  const handleAdd = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      await customerApi.addAddress(form)
      queryClient.invalidateQueries({ queryKey: ['addresses'] })
      setShowForm(false)
      setForm({ label: '', street: '', city: '', state: '', zipCode: '', isDefault: false })
    } catch {}
  }

  const handleDelete = async (id: string) => {
    await customerApi.deleteAddress(id)
    queryClient.invalidateQueries({ queryKey: ['addresses'] })
  }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 py-8">
      <Link href="/profile" className="flex items-center gap-2 text-coffee-500 hover:text-mooshi-600 mb-6 text-sm">
        <ArrowLeft className="w-4 h-4" /> Back to Profile
      </Link>
      <div className="flex items-center justify-between mb-6">
        <h1 className="font-display text-3xl font-bold text-coffee-900">Addresses</h1>
        <button onClick={() => setShowForm(!showForm)} className="btn-primary text-sm py-2 px-4 flex items-center gap-2">
          <Plus className="w-4 h-4" /> Add
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleAdd} className="card mb-6 space-y-3">
          <h3 className="font-semibold mb-2">New Address</h3>
          <input className="input-field" placeholder="Label (e.g. Home)" value={form.label}
            onChange={e => setForm({ ...form, label: e.target.value })} required />
          <input className="input-field" placeholder="Street" value={form.street}
            onChange={e => setForm({ ...form, street: e.target.value })} required />
          <div className="grid grid-cols-3 gap-3">
            <input className="input-field" placeholder="City" value={form.city}
              onChange={e => setForm({ ...form, city: e.target.value })} required />
            <input className="input-field" placeholder="State" value={form.state}
              onChange={e => setForm({ ...form, state: e.target.value })} />
            <input className="input-field" placeholder="Zip Code" value={form.zipCode}
              onChange={e => setForm({ ...form, zipCode: e.target.value })} required />
          </div>
          <label className="flex items-center gap-2 text-sm">
            <input type="checkbox" checked={form.isDefault}
              onChange={e => setForm({ ...form, isDefault: e.target.checked })}
              className="rounded border-coffee-300 text-mooshi-600" />
            Set as default
          </label>
          <button type="submit" className="btn-primary w-full text-sm">Save Address</button>
        </form>
      )}

      {addresses.length === 0 ? (
        <div className="text-center py-12 text-coffee-500">
          <MapPin className="w-12 h-12 mx-auto mb-3 text-coffee-300" />
          <p>No addresses added yet</p>
        </div>
      ) : (
        <div className="space-y-3">
          {addresses.map(addr => (
            <div key={addr.id} className="card flex items-start justify-between">
              <div>
                <div className="flex items-center gap-2">
                  <h3 className="font-semibold text-coffee-900">{addr.label}</h3>
                  {addr.isDefault && <span className="text-xs bg-mooshi-100 text-mooshi-700 px-2 py-0.5 rounded-full">Default</span>}
                </div>
                <p className="text-sm text-coffee-500 mt-1">
                  {addr.street}, {addr.city}, {addr.state} {addr.zipCode}, {addr.country}
                </p>
              </div>
              <button onClick={() => handleDelete(addr.id)} className="text-red-400 hover:text-red-600 text-sm">
                Remove
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
