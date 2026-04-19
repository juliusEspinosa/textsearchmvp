import { useQuery, keepPreviousData } from '@tanstack/react-query'
import { searchPurchases } from '../api/purchasesApi'

export function usePurchaseSearch(buyer: string, page: number) {
  return useQuery({
    queryKey: ['purchases', 'search', buyer, page],
    queryFn: () => searchPurchases(buyer, page),
    enabled: buyer.trim().length > 0,
    placeholderData: keepPreviousData,
  })
}
