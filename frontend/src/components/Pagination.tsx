interface PaginationProps {
  page: number
  totalPages: number
  totalElements: number
  isFirst: boolean
  isLast: boolean
  onPrevious: () => void
  onNext: () => void
}

export default function Pagination({ page, totalPages, totalElements, isFirst, isLast, onPrevious, onNext }: PaginationProps) {
  return (
    <div className="mt-4 flex items-center justify-between">
      <button
        onClick={onPrevious}
        disabled={isFirst}
        className="rounded bg-gray-100 px-3 py-1.5 text-sm text-gray-700 hover:bg-gray-200 disabled:opacity-40 disabled:cursor-not-allowed"
      >
        Previous
      </button>
      <div className="text-center">
        <span className="text-sm text-gray-500">
          Page {page + 1} of {Math.max(totalPages, 1)}
        </span>
        <span className="block text-xs text-gray-400">
          {totalElements} total item{totalElements !== 1 ? 's' : ''}
        </span>
      </div>
      <button
        onClick={onNext}
        disabled={isLast}
        className="rounded bg-gray-100 px-3 py-1.5 text-sm text-gray-700 hover:bg-gray-200 disabled:opacity-40 disabled:cursor-not-allowed"
      >
        Next
      </button>
    </div>
  )
}
