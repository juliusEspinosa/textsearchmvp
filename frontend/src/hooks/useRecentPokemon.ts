import { useQuery, keepPreviousData } from '@tanstack/react-query'
import { listPokemon } from '../api/pokemonApi'

export function useRecentPokemon(page: number) {
  return useQuery({
    queryKey: ['pokemon', 'recent', page],
    queryFn: () => listPokemon(page, 20),
    placeholderData: keepPreviousData,
  })
}
