import { useQuery, keepPreviousData } from '@tanstack/react-query'
import { listDigimon } from '../api/digimonApi'

export function useRecentDigimon(page: number) {
  return useQuery({
    queryKey: ['digimon', 'recent', page],
    queryFn: () => listDigimon(page, 10),
    placeholderData: keepPreviousData,
  })
}
