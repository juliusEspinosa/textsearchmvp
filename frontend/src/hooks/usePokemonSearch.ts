import { useQuery, keepPreviousData } from '@tanstack/react-query'
import { searchPokemon } from '../api/pokemonApi'

export function usePokemonSearch(query: string, page: number) {
  return useQuery({
    queryKey: ['pokemon', 'search', query, page],
    queryFn: () => searchPokemon(query, page),
    enabled: query.trim().length > 0,
    placeholderData: keepPreviousData,
  })
}
