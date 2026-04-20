import { useState } from 'react'
import { usePurchaseSearch } from '../hooks/usePurchaseSearch'
import { useRecentPurchases } from '../hooks/useRecentPurchases'
import { useDebouncedValue } from '../hooks/useDebouncedValue'
import ItemNotesModal from '../components/ItemNotesModal'
import Pagination from '../components/Pagination'
import type { PurchaseResponse } from '../api/purchasesApi'
import DOMPurify from 'dompurify'

function PurchaseTable({
  purchases,
  onItemClick,
}: {
  purchases: PurchaseResponse[]
  onItemClick: (itemId: string) => void
}) {
  return (
    <table className="w-full border border-gray-200 rounded-lg bg-white text-sm">
      <thead>
        <tr className="bg-gray-50 text-left text-gray-600">
          <th className="px-4 py-3 font-medium">Buyer</th>
          <th className="px-4 py-3 font-medium">Item</th>
          <th className="px-4 py-3 font-medium text-right">Qty</th>
        </tr>
      </thead>
      <tbody className="divide-y divide-gray-200">
        {purchases.map((purchase) => (
          <tr key={purchase.purchaseId} className="hover:bg-gray-50">
            <td className="px-4 py-3 text-gray-900">{purchase.buyer}</td>
            <td className="px-4 py-3">
              <button
                onClick={() => onItemClick(purchase.itemId)}
                className="text-blue-600 hover:text-blue-800 hover:underline cursor-pointer bg-transparent border-none p-0 text-sm text-left"
              >
                {purchase.itemName}
              </button>
            </td>
            <td className="px-4 py-3 text-right text-gray-900">{purchase.quantity}</td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}

export default function PurchasesPage() {
  const [query, setQuery] = useState('')
  const [page, setPage] = useState(0)
  const [selectedItemId, setSelectedItemId] = useState<string | null>(null)
  const debouncedQuery = useDebouncedValue(query, 300)
  const { data, isLoading, isError, isFetching } = usePurchaseSearch(debouncedQuery, page)
  const { data: recentData } = useRecentPurchases(page)

  const isSearching = debouncedQuery.trim().length > 0
  const activeData = isSearching ? data : recentData

  function handleQueryChange(value: string) {
    setQuery(value)
    setPage(0)
  }

  return (
    <div className="mx-auto max-w-3xl px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Purchases</h1>

      <div className="relative">
        <input
          type="text"
          placeholder="Search by item name or notes..."
          value={query}
          onChange={(e) => handleQueryChange(e.target.value)}
          className="w-full rounded-lg border border-gray-300 px-4 py-3 text-gray-900 placeholder-gray-400 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-200"
        />
        {isFetching && (
          <span className="absolute right-3 top-3.5 text-xs text-gray-400">searching...</span>
        )}
      </div>

      <div className="mt-6">
        {!isSearching && recentData?.content && recentData.content.length > 0 && (
          <>
            <p className="text-gray-500 text-xs mb-3 uppercase tracking-wide font-medium">
              Recent purchases
            </p>
            <PurchaseTable purchases={recentData.content} onItemClick={setSelectedItemId} />
          </>
        )}

        {!isSearching && !recentData && (
          <p className="text-gray-500 text-sm">Type to search purchases by item name or notes.</p>
        )}

        {isSearching && isLoading && (
          <p className="text-gray-500 text-sm">Searching...</p>
        )}

        {isSearching && isError && (
          <p className="text-red-600 text-sm">Something went wrong. Please try again.</p>
        )}

        {isSearching && data?.content && data.content.length === 0 && (
          <p className="text-gray-500 text-sm">No purchases found for &ldquo;{DOMPurify.sanitize(debouncedQuery)}&rdquo;.</p>
        )}

        {isSearching && data?.content && data.content.length > 0 && (
          <>
            <p className="text-gray-500 text-xs mb-3">
              {data.totalElements} result{data.totalElements !== 1 ? 's' : ''} found
            </p>
            <PurchaseTable purchases={data.content} onItemClick={setSelectedItemId} />
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

      {selectedItemId && (
        <ItemNotesModal itemId={selectedItemId} onClose={() => setSelectedItemId(null)} />
      )}
    </div>
  )
}
