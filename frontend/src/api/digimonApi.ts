import axios from 'axios'
import type { PageResponse } from './itemsApi'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
})

export interface DigimonResponse {
  digimonId: string
  name: string
  wikiUrl: string | null
  imageUrl: string | null
  description: string | null
}

export function digimonImageUrl(wikiFileUrl: string): string {
  const filename = wikiFileUrl.replace(/^.*\/File:/, '')
  const baseUrl = import.meta.env.VITE_API_BASE_URL || ''
  return `${baseUrl}/api/image-proxy?file=${encodeURIComponent(filename)}`
}

export async function searchDigimon(
  name: string | undefined,
  description: string | undefined,
  mode: 'and' | 'or',
  page = 0,
  size = 20,
): Promise<PageResponse<DigimonResponse>> {
  const params: Record<string, string | number> = { mode, page, size }
  if (name) params.name = name
  if (description) params.description = description
  const { data } = await api.get<PageResponse<DigimonResponse>>('/api/digimon/search', { params })
  return data
}

export async function listDigimon(
  page = 0,
  size = 20,
): Promise<PageResponse<DigimonResponse>> {
  const { data } = await api.get<PageResponse<DigimonResponse>>('/api/digimon', {
    params: { page, size },
  })
  return data
}

export async function getDigimon(id: string): Promise<DigimonResponse> {
  const { data } = await api.get<DigimonResponse>(`/api/digimon/${id}`)
  return data
}
