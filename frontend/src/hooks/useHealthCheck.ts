import { useQuery } from '@tanstack/react-query'
import { checkHealth } from '../api/healthApi'

export function useHealthCheck() {
  return useQuery({
    queryKey: ['health'],
    queryFn: checkHealth,
    refetchInterval: 30_000,
    retry: 1,
  })
}
