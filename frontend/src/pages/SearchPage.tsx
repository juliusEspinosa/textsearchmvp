import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useItemSearch } from '../hooks/useItemSearch'
import { useRecentItems } from '../hooks/useRecentItems'
import { useDebouncedValue } from '../hooks/useDebouncedValue'
import Pagination from '../components/Pagination'
import type { ItemResponse } from '../api/itemsApi'
import DOMPurify from 'dompurify'

function ItemList({ items }: { items: ItemResponse[] }) {
  return (
    <ul className="divide-y divide-gray-200 rounded-lg border border-gray-200 bg-white">
      {items.map((item) => (
        <li key={item.itemId}>
          <Link
            to={`/items/${item.itemId}`}
            className="block px-4 py-3 hover:bg-gray-50 no-underline"
          >
            <p className="font-medium text-gray-900">{item.itemName}</p>
            {item.notes && (
              <p className="mt-1 text-sm text-gray-500 line-clamp-2">{item.notes}</p>
            )}
          </Link>
        </li>
      ))}
    </ul>
  )
}

export default function SearchPage() {
  const [query, setQuery] = useState('')
  const [page, setPage] = useState(0)
  const debouncedQuery = useDebouncedValue(query, 300)
  const { data, isLoading, isError, isFetching } = useItemSearch(debouncedQuery, page)
  const { data: recentData } = useRecentItems(page)

  const isSearching = debouncedQuery.trim().length > 0
  const activeData = isSearching ? data : recentData

  function handleQueryChange(value: string) {
    setQuery(value)
    setPage(0)
  }

  return (
    <div className="mx-auto max-w-3xl px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Search Items</h1>

      <div className="relative">
        <input
          type="text"
          placeholder="Search by name or notes..."
          value={query}
          onChange={(e) => handleQueryChange(e.target.value)}
          className="w-full rounded-lg border border-gray-300 px-4 py-3 text-gray-900 placeholder-gray-400 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-200"
        />
        {isFetching && (
          <span className="absolute right-3 top-3.5 text-xs text-gray-400">searching...</span>
        )}
      </div>

      <div className="mt-6">
        {!isSearching && !recentData && (
          <p className="text-gray-500 text-sm">Type to search items by name or notes.</p>
        )}

        {!isSearching && recentData && recentData.content.length > 0 && (
          <>
            <p className="text-gray-500 text-xs mb-3 uppercase tracking-wide font-medium">
              Recent items
            </p>
            <ItemList items={recentData.content} />
          </>
        )}

        {isSearching && isLoading && (
          <p className="text-gray-500 text-sm">Searching...</p>
        )}

        {isSearching && isError && (
          <p className="text-red-600 text-sm">Something went wrong. Please try again.</p>
        )}

        {isSearching && data && data.content.length === 0 && (
          <p className="text-gray-500 text-sm">No items found for &ldquo;{DOMPurify.sanitize(debouncedQuery)}&rdquo;.</p>
        )}

        {isSearching && data && data.content.length > 0 && (
          <>
            <p className="text-gray-500 text-xs mb-3">
              {data.totalElements} result{data.totalElements !== 1 ? 's' : ''} found
              {data.searchDurationMs != null && (
                <span className="ml-1 text-gray-400">in {data.searchDurationMs}ms</span>
              )}
            </p>
            <ItemList items={data.content} />
          </>
        )}

        {activeData && activeData.content.length > 0 && (
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
    </div>
  )
}
