import { useState } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useItem } from '../hooks/useItem'
import { updateItem, deleteItem } from '../api/itemsApi'

export default function ItemDetailPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const { data: item, isLoading, isError } = useItem(id)

  const [editing, setEditing] = useState(false)
  const [editName, setEditName] = useState('')
  const [editNotes, setEditNotes] = useState('')
  const [confirmDelete, setConfirmDelete] = useState(false)

  const updateMutation = useMutation({
    mutationFn: () => updateItem(id!, { itemName: editName, notes: editNotes || null }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['items', id] })
      setEditing(false)
    },
  })

  const deleteMutation = useMutation({
    mutationFn: () => deleteItem(id!),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['items'] })
      navigate('/')
    },
  })

  function startEditing() {
    if (!item) return
    setEditName(item.itemName)
    setEditNotes(item.notes ?? '')
    setEditing(true)
  }

  function cancelEditing() {
    setEditing(false)
    updateMutation.reset()
  }

  return (
    <div className="mx-auto max-w-3xl px-4 py-8">
      <Link to="/" className="text-blue-600 hover:underline text-sm">&larr; Back to search</Link>

      {isLoading && (
        <p className="mt-6 text-gray-500 text-sm">Loading...</p>
      )}

      {isError && (
        <div className="mt-6 rounded-lg border border-red-200 bg-red-50 px-4 py-3">
          <p className="text-red-700 text-sm">Item not found or an error occurred.</p>
        </div>
      )}

      {item && !editing && (
        <div className="mt-6">
          <div className="flex items-start justify-between">
            <h1 className="text-2xl font-bold text-gray-900">{item.itemName}</h1>
            <div className="flex gap-2 ml-4 shrink-0">
              <button
                onClick={startEditing}
                className="rounded bg-blue-50 px-3 py-1.5 text-sm text-blue-700 hover:bg-blue-100"
              >
                Edit
              </button>
              {!confirmDelete ? (
                <button
                  onClick={() => setConfirmDelete(true)}
                  className="rounded bg-red-50 px-3 py-1.5 text-sm text-red-700 hover:bg-red-100"
                >
                  Delete
                </button>
              ) : (
                <div className="flex gap-1">
                  <button
                    onClick={() => deleteMutation.mutate()}
                    disabled={deleteMutation.isPending}
                    className="rounded bg-red-600 px-3 py-1.5 text-sm text-white hover:bg-red-700 disabled:opacity-50"
                  >
                    {deleteMutation.isPending ? 'Deleting...' : 'Confirm'}
                  </button>
                  <button
                    onClick={() => setConfirmDelete(false)}
                    className="rounded bg-gray-100 px-3 py-1.5 text-sm text-gray-700 hover:bg-gray-200"
                  >
                    Cancel
                  </button>
                </div>
              )}
            </div>
          </div>

          {deleteMutation.isError && (
            <p className="mt-2 text-red-600 text-sm">Failed to delete. Please try again.</p>
          )}

          <div className="mt-6 rounded-lg border border-gray-200 bg-white p-5">
            <h2 className="text-sm font-medium text-gray-500 uppercase tracking-wide">Notes</h2>
            {item.notes ? (
              <p className="mt-2 text-gray-800 whitespace-pre-wrap">{item.notes}</p>
            ) : (
              <p className="mt-2 text-gray-400 italic">No notes.</p>
            )}
          </div>

          <div className="mt-4 flex gap-6 text-xs text-gray-400">
            <span>Created {new Date(item.createdAt).toLocaleDateString()}</span>
            <span>Updated {new Date(item.updatedAt).toLocaleDateString()}</span>
          </div>
        </div>
      )}

      {item && editing && (
        <div className="mt-6">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">Edit Item</h2>

          <label className="block text-sm font-medium text-gray-700 mb-1">Name</label>
          <input
            type="text"
            value={editName}
            onChange={(e) => setEditName(e.target.value)}
            className="w-full rounded-lg border border-gray-300 px-4 py-2 text-gray-900 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-200 mb-4"
          />

          <label className="block text-sm font-medium text-gray-700 mb-1">Notes</label>
          <textarea
            value={editNotes}
            onChange={(e) => setEditNotes(e.target.value)}
            rows={6}
            className="w-full rounded-lg border border-gray-300 px-4 py-2 text-gray-900 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-200 mb-4"
          />

          {updateMutation.isError && (
            <p className="text-red-600 text-sm mb-3">Failed to save. Please try again.</p>
          )}

          <div className="flex gap-2">
            <button
              onClick={() => updateMutation.mutate()}
              disabled={updateMutation.isPending || editName.trim().length === 0}
              className="rounded bg-blue-600 px-4 py-2 text-sm text-white hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {updateMutation.isPending ? 'Saving...' : 'Save'}
            </button>
            <button
              onClick={cancelEditing}
              className="rounded bg-gray-100 px-4 py-2 text-sm text-gray-700 hover:bg-gray-200"
            >
              Cancel
            </button>
          </div>
        </div>
      )}
    </div>
  )
}
