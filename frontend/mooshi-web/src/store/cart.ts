import { create } from 'zustand'
import type { CartItem } from '@/types'

interface CartState {
  items: CartItem[]
  addItem: (item: CartItem) => void
  removeItem: (productId: string) => void
  updateQuantity: (productId: string, quantity: number) => void
  clearCart: () => void
  getTotal: () => { subtotal: number; count: number }
}

export const useCartStore = create<CartState>((set, get) => ({
  items: [],
  addItem: (item) => set((state) => {
    const existing = state.items.findIndex(
      i => i.productId === item.productId && i.size === item.size
    )
    if (existing >= 0) {
      const updated = [...state.items]
      updated[existing] = {
        ...updated[existing],
        quantity: updated[existing].quantity + item.quantity,
      }
      return { items: updated }
    }
    return { items: [...state.items, item] }
  }),
  removeItem: (productId) => set((state) => ({
    items: state.items.filter(i => i.productId !== productId),
  })),
  updateQuantity: (productId, quantity) => set((state) => ({
    items: quantity <= 0
      ? state.items.filter(i => i.productId !== productId)
      : state.items.map(i => i.productId === productId ? { ...i, quantity } : i),
  })),
  clearCart: () => set({ items: [] }),
  getTotal: () => {
    const items = get().items
    return {
      subtotal: items.reduce((sum, i) => sum + i.unitPrice * i.quantity, 0),
      count: items.reduce((sum, i) => sum + i.quantity, 0),
    }
  },
}))
