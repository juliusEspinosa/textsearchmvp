import { useQuery } from '@tanstack/react-query'
import { getItem } from '../api/itemsApi'

export function useItem(id: string | undefined) {
  return useQuery({
    queryKey: ['items', id],
    queryFn: () => getItem(id!),
    enabled: !!id,
  })
}
