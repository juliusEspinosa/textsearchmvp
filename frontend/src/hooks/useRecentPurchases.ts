import { useQuery, keepPreviousData } from '@tanstack/react-query'
import { listPurchases } from '../api/purchasesApi'

export function useRecentPurchases(page: number) {
  return useQuery({
    queryKey: ['purchases', 'recent', page],
    queryFn: () => listPurchases(page, 20),
    placeholderData: keepPreviousData,
  })
}
