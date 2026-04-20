import { useState } from 'react'
import { useDigimonSearch } from '../hooks/useDigimonSearch'
import { useRecentDigimon } from '../hooks/useRecentDigimon'
import { useDebouncedValue } from '../hooks/useDebouncedValue'
import DigimonModal from '../components/DigimonModal'
import Pagination from '../components/Pagination'
import type { DigimonResponse } from '../api/digimonApi'
import { digimonImageUrl } from '../api/digimonApi'

function DigimonTable({
  digimon,
  onNameClick,
}: {
  digimon: DigimonResponse[]
  onNameClick: (id: string) => void
}) {
  return (
    <table className="w-full border border-gray-200 rounded-lg bg-white text-sm">
      <thead>
        <tr className="bg-gray-50 text-left text-gray-600">
          <th className="px-4 py-3 font-medium w-20">Image</th>
          <th className="px-4 py-3 font-medium w-48">Name</th>
          <th className="px-4 py-3 font-medium">Description</th>
        </tr>
      </thead>
      <tbody className="divide-y divide-gray-200">
        {digimon.map((d) => (
          <tr key={d.digimonId} className="hover:bg-gray-50">
            <td className="px-4 py-3">
              {d.imageUrl && (
                <img
                  src={digimonImageUrl(d.imageUrl)}
                  alt={d.name}
                  className="h-14 w-14 rounded object-contain"
                  onError={(e) => { (e.target as HTMLImageElement).style.display = 'none' }}
                />
              )}
            </td>
            <td className="px-4 py-3">
              <button
                onClick={() => onNameClick(d.digimonId)}
                className="text-blue-600 hover:text-blue-800 hover:underline cursor-pointer bg-transparent border-none p-0 text-sm text-left font-medium"
              >
                {d.name}
              </button>
            </td>
            <td className="px-4 py-3 text-gray-600 text-sm line-clamp-3">
              {d.description}
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}

export default function DigimonPage() {
  const [nameQuery, setNameQuery] = useState('')
  const [descQuery, setDescQuery] = useState('')
  const [mode, setMode] = useState<'and' | 'or' | 'name' | 'desc'>('and')
  const [page, setPage] = useState(0)
  const [selectedId, setSelectedId] = useState<string | null>(null)

  const debouncedName = useDebouncedValue(nameQuery, 300)
  const debouncedDesc = useDebouncedValue(descQuery, 300)

  const hasName = debouncedName.trim().length > 0
  const hasDesc = debouncedDesc.trim().length > 0
  const isSearching = mode === 'name' ? hasName
    : mode === 'desc' ? hasDesc
    : (hasName || hasDesc)
  const { data, isLoading, isError, isFetching } = useDigimonSearch(debouncedName, debouncedDesc, mode, page)
  const { data: recentData } = useRecentDigimon(page)

  const activeData = isSearching ? data : recentData

  function handleSearchChange() {
    setPage(0)
  }

  return (
    <div className="mx-auto max-w-6xl px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Digimon</h1>

      <div className="space-y-3">
        <div className="relative">
          <input
            type="text"
            placeholder="Search by name..."
            value={nameQuery}
            onChange={(e) => { setNameQuery(e.target.value); handleSearchChange() }}
            className="w-full rounded-lg border border-gray-300 px-4 py-3 text-gray-900 placeholder-gray-400 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-200"
          />
        </div>

        <div className="relative">
          <input
            type="text"
            placeholder="Search by description..."
            value={descQuery}
            onChange={(e) => { setDescQuery(e.target.value); handleSearchChange() }}
            className="w-full rounded-lg border border-gray-300 px-4 py-3 text-gray-900 placeholder-gray-400 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-200"
          />
          {isFetching && (
            <span className="absolute right-3 top-3.5 text-xs text-gray-400">searching...</span>
          )}
        </div>

        <div className="flex items-center gap-3">
          <span className="text-sm text-gray-600">Search mode:</span>
          <div className="flex rounded-lg border border-gray-300 overflow-hidden">
            {(['and', 'or', 'name', 'desc'] as const).map((m) => (
              <button
                key={m}
                onClick={() => { setMode(m); setPage(0) }}
                className={`px-4 py-1.5 text-sm ${mode === m ? 'bg-blue-600 text-white' : 'bg-white text-gray-700 hover:bg-gray-50'}`}
              >
                {m === 'name' ? 'Name only' : m === 'desc' ? 'Desc only' : m.toUpperCase()}
              </button>
            ))}
          </div>
        </div>
      </div>

      <div className="mt-6">
        {!isSearching && recentData?.content && recentData.content.length > 0 && (
          <>
            <p className="text-gray-500 text-xs mb-3 uppercase tracking-wide font-medium">
              All Digimon
            </p>
            <DigimonTable digimon={recentData.content} onNameClick={setSelectedId} />
          </>
        )}

        {!isSearching && !recentData && (
          <p className="text-gray-500 text-sm">Search Digimon by name, description, or both.</p>
        )}

        {isSearching && isLoading && (
          <p className="text-gray-500 text-sm">Searching...</p>
        )}

        {isSearching && isError && (
          <p className="text-red-600 text-sm">Something went wrong. Please try again.</p>
        )}

        {isSearching && data?.content && data.content.length === 0 && (
          <p className="text-gray-500 text-sm">No Digimon found.</p>
        )}

        {isSearching && data?.content && data.content.length > 0 && (
          <>
            <p className="text-gray-500 text-xs mb-3">
              {data.totalElements} result{data.totalElements !== 1 ? 's' : ''} found
            </p>
            <DigimonTable digimon={data.content} onNameClick={setSelectedId} />
          </>
        )}

        {activeData?.content && activeData.content.length > 0 && (
          <Pagination
            page={activeData.number}
            totalPages={activeData.totalPages}
            totalElements={activeData.totalElements}
            isFirst={activeData.first}
            isLast={activeData.last}
            onPrevious={() => setPage((p) => Math.max(0, p - 1))}
            onNext={() => setPage((p) => p + 1)}
          />
        )}
      </div>

      {selectedId && (
        <DigimonModal digimonId={selectedId} onClose={() => setSelectedId(null)} />
      )}
    </div>
  )
}
