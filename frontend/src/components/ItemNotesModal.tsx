import { useItem } from '../hooks/useItem'

interface ItemNotesModalProps {
  itemId: string
  onClose: () => void
}

export default function ItemNotesModal({ itemId, onClose }: ItemNotesModalProps) {
  const { data: item, isLoading, isError } = useItem(itemId)

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/40"
      onClick={onClose}
    >
      <div
        className="mx-4 w-full max-w-lg rounded-xl bg-white p-6 shadow-xl"
        onClick={(e) => e.stopPropagation()}
      >
        {isLoading && <p className="text-gray-500 text-sm">Loading...</p>}

        {isError && <p className="text-red-600 text-sm">Failed to load item.</p>}

        {item && (
          <>
            <div className="flex items-start justify-between">
              <h2 className="text-lg font-bold text-gray-900">{item.itemName}</h2>
              <button
                onClick={onClose}
                className="ml-4 shrink-0 rounded p-1 text-gray-400 hover:bg-gray-100 hover:text-gray-600"
              >
                ✕
              </button>
            </div>

            <div className="mt-4 rounded-lg border border-gray-200 bg-gray-50 p-4">
              <h3 className="text-xs font-medium text-gray-500 uppercase tracking-wide mb-2">Notes</h3>
              {item.notes ? (
                <p className="text-gray-800 text-sm whitespace-pre-wrap">{item.notes}</p>
              ) : (
                <p className="text-gray-400 text-sm italic">No notes.</p>
              )}
            </div>

            <div className="mt-3 flex gap-4 text-xs text-gray-400">
              <span>Created {new Date(item.createdAt).toLocaleDateString()}</span>
              <span>Updated {new Date(item.updatedAt).toLocaleDateString()}</span>
            </div>
          </>
        )}
      </div>
    </div>
  )
}
