import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
})

export interface ItemResponse {
  itemId: string
  itemName: string
  notes: string | null
  createdAt: string
  updatedAt: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
  first: boolean
  last: boolean
}

export async function getItem(id: string): Promise<ItemResponse> {
  const { data } = await api.get<ItemResponse>(`/api/items/${id}`)
  return data
}

export async function listItems(
  page = 0,
  size = 20,
): Promise<PageResponse<ItemResponse>> {
  const { data } = await api.get<PageResponse<ItemResponse>>('/api/items', {
    params: { page, size },
  })
  return data
}

export async function updateItem(
  id: string,
  body: { itemName?: string; notes?: string | null },
): Promise<ItemResponse> {
  const { data } = await api.patch<ItemResponse>(`/api/items/${id}`, body)
  return data
}

export async function deleteItem(id: string): Promise<void> {
  await api.delete(`/api/items/${id}`)
}

export interface SearchResponse extends PageResponse<ItemResponse> {
  searchDurationMs: number | null
}

export async function searchItems(
  query: string,
  page = 0,
  size = 20,
): Promise<SearchResponse> {
  const response = await api.get<PageResponse<ItemResponse>>('/api/items/search', {
    params: { q: query, page, size },
  })
  const durationHeader = response.headers['x-search-duration-ms']
  return {
    ...response.data,
    searchDurationMs: durationHeader ? parseInt(durationHeader, 10) : null,
  }
}
