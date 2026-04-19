import axios from 'axios'
import type { PageResponse } from './itemsApi'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
})

export interface PurchaseResponse {
  purchaseId: string
  buyer: string
  itemId: string
  itemName: string
  quantity: number
  purchasedAt: string
}

export async function searchPurchases(
  query: string,
  page = 0,
  size = 20,
): Promise<PageResponse<PurchaseResponse>> {
  const { data } = await api.get<PageResponse<PurchaseResponse>>('/api/purchases/search', {
    params: { q: query, page, size },
  })
  return data
}

export async function listPurchases(
  page = 0,
  size = 20,
): Promise<PageResponse<PurchaseResponse>> {
  const { data } = await api.get<PageResponse<PurchaseResponse>>('/api/purchases', {
    params: { page, size },
  })
  return data
}
