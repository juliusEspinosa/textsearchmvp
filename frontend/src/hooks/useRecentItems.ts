import { useQuery, keepPreviousData } from '@tanstack/react-query'
import { listItems } from '../api/itemsApi'

export function useRecentItems(page: number) {
  return useQuery({
    queryKey: ['items', 'recent', page],
    queryFn: () => listItems(page, 20),
    placeholderData: keepPreviousData,
  })
}
