import { useQuery, keepPreviousData } from '@tanstack/react-query'
import { searchDigimon } from '../api/digimonApi'

export function useDigimonSearch(
  name: string,
  description: string,
  mode: 'and' | 'or' | 'name' | 'desc',
  page: number,
) {
  const hasName = name.trim().length > 0
  const hasDesc = description.trim().length > 0

  const effectiveName = mode === 'desc' ? undefined : (hasName ? name : undefined)
  const effectiveDesc = mode === 'name' ? undefined : (hasDesc ? description : undefined)
  const apiMode = mode === 'name' || mode === 'desc' ? 'and' : mode

  const enabled = !!(effectiveName || effectiveDesc)

  return useQuery({
    queryKey: ['digimon', 'search', name, description, mode, page],
    queryFn: () => searchDigimon(effectiveName, effectiveDesc, apiMode, page, 10),
    enabled,
    placeholderData: keepPreviousData,
  })
}
