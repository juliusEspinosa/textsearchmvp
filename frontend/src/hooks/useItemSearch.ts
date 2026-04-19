import { useQuery, keepPreviousData } from '@tanstack/react-query'
import { searchItems } from '../api/itemsApi'

export function useItemSearch(query: string, page: number) {
  return useQuery({
    queryKey: ['items', 'search', query, page],
    queryFn: () => searchItems(query, page),
    enabled: query.trim().length > 0,
    placeholderData: keepPreviousData,
  })
}
