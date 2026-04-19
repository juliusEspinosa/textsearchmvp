import axios from 'axios'
import type { PageResponse } from './itemsApi'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
})

export interface PokemonResponse {
  pokemonId: string
  pokedexNumber: number
  name: string
  type1: string
  type2: string | null
  total: number
  hp: number
  attack: number
  defense: number
  spAtk: number
  spDef: number
  speed: number
  generation: number
  legendary: boolean
}

export async function searchPokemon(
  query: string,
  page = 0,
  size = 20,
): Promise<PageResponse<PokemonResponse>> {
  const { data } = await api.get<PageResponse<PokemonResponse>>('/api/pokemon/search', {
    params: { q: query, page, size },
  })
  return data
}

export async function listPokemon(
  page = 0,
  size = 20,
): Promise<PageResponse<PokemonResponse>> {
  const { data } = await api.get<PageResponse<PokemonResponse>>('/api/pokemon', {
    params: { page, size },
  })
  return data
}

export async function getPokemon(id: string): Promise<PokemonResponse> {
  const { data } = await api.get<PokemonResponse>(`/api/pokemon/${id}`)
  return data
}
