import { useState } from 'react'
import { usePurchaseSearch } from '../hooks/usePurchaseSearch'
import { useRecentPurchases } from '../hooks/useRecentPurchases'
import { useDebouncedValue } from '../hooks/useDebouncedValue'
import ItemNotesModal from '../components/ItemNotesModal'
import Pagination from '../components/Pagination'
import type { PurchaseResponse } from '../api/purchasesApi'
import DOMPurify from 'dompurify'
import { jsPDF } from 'jspdf'
import autoTable from 'jspdf-autotable'

function PurchaseTable({
  purchases,
  onItemClick,
  selectedIds,
  onToggle,
}: {
  purchases: PurchaseResponse[]
  onItemClick: (itemId: string) => void
  selectedIds: Set<string>
  onToggle: (purchaseId: string) => void
}) {
  return (
    <table className="w-full border border-gray-200 rounded-lg bg-white text-sm">
      <thead>
        <tr className="bg-gray-50 text-left text-gray-600">
          <th className="px-4 py-3 font-medium">Buyer</th>
          <th className="px-4 py-3 font-medium">Item</th>
          <th className="px-4 py-3 font-medium">Notes</th>
          <th className="px-4 py-3 font-medium text-right">Qty</th>
          <th className="px-4 py-3 font-medium text-center">Add to Report</th>
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
            <td className="px-4 py-3 text-gray-500">{purchase.itemNotes ?? '—'}</td>
            <td className="px-4 py-3 text-right text-gray-900">{purchase.quantity}</td>
            <td className="px-4 py-3 text-center">
              <input
                type="checkbox"
                checked={selectedIds.has(purchase.purchaseId)}
                onChange={() => onToggle(purchase.purchaseId)}
                className="h-4 w-4 rounded border-gray-300 text-blue-600 focus:ring-blue-500"
              />
            </td>
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
  const [reportIds, setReportIds] = useState<Set<string>>(new Set())
  const debouncedQuery = useDebouncedValue(query, 300)
  const { data, isLoading, isError, isFetching } = usePurchaseSearch(debouncedQuery, page)
  const { data: recentData } = useRecentPurchases(page)

  const isSearching = debouncedQuery.trim().length > 0
  const activeData = isSearching ? data : recentData

  function handleQueryChange(value: string) {
    setQuery(value)
    setPage(0)
  }

  function handleToggleReport(purchaseId: string) {
    setReportIds((prev) => {
      const next = new Set(prev)
      if (next.has(purchaseId)) {
        next.delete(purchaseId)
      } else {
        next.add(purchaseId)
      }
      return next
    })
  }

  function handleGenerateReport() {
    const purchases = activeData?.content?.filter((p) => reportIds.has(p.purchaseId)) ?? []
    if (purchases.length === 0) return

    const doc = new jsPDF()
    doc.setFontSize(16)
    doc.text('Purchases Report', 14, 20)
    doc.setFontSize(10)
    doc.text(`Generated: ${new Date().toLocaleDateString()}`, 14, 28)

    autoTable(doc, {
      startY: 35,
      head: [['Buyer', 'Item', 'Notes', 'Qty']],
      body: purchases.map((p) => [
        p.buyer,
        p.itemName,
        p.itemNotes ?? '—',
        p.quantity.toString(),
      ]),
    })

    doc.save('purchases-report.pdf')
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
            <div className="flex items-center justify-between mb-3">
              <p className="text-gray-500 text-xs uppercase tracking-wide font-medium">
                Recent purchases
              </p>
              <button
                onClick={handleGenerateReport}
                disabled={reportIds.size === 0}
                className="text-blue-600 hover:text-blue-800 text-xs font-medium disabled:text-gray-300 disabled:cursor-not-allowed"
              >
                Generate Report ({reportIds.size})
              </button>
            </div>
            <PurchaseTable purchases={recentData.content} onItemClick={setSelectedItemId} selectedIds={reportIds} onToggle={handleToggleReport} />
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
            <div className="flex items-center justify-between mb-3">
              <p className="text-gray-500 text-xs">
                {data.totalElements} result{data.totalElements !== 1 ? 's' : ''} found
              </p>
              <button
                onClick={handleGenerateReport}
                disabled={reportIds.size === 0}
                className="text-blue-600 hover:text-blue-800 text-xs font-medium disabled:text-gray-300 disabled:cursor-not-allowed"
              >
                Generate Report ({reportIds.size})
              </button>
            </div>
            <PurchaseTable purchases={data.content} onItemClick={setSelectedItemId} selectedIds={reportIds} onToggle={handleToggleReport} />
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
