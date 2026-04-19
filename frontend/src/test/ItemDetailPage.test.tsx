import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { vi, describe, it, expect, beforeEach } from 'vitest'
import { Routes, Route } from 'react-router-dom'
import { renderWithProviders } from './testUtils'
import ItemDetailPage from '../pages/ItemDetailPage'

vi.mock('../api/itemsApi', () => ({
  getItem: vi.fn(),
  updateItem: vi.fn(),
  deleteItem: vi.fn(),
}))

import { getItem, updateItem, deleteItem } from '../api/itemsApi'

const mockGetItem = vi.mocked(getItem)
const mockUpdateItem = vi.mocked(updateItem)
const mockDeleteItem = vi.mocked(deleteItem)

const sampleItem = {
  itemId: '123e4567-e89b-12d3-a456-426614174000',
  itemName: 'Wireless Mouse',
  notes: 'Ergonomic bluetooth mouse with silent clicks',
  createdAt: '2026-01-15T10:30:00Z',
  updatedAt: '2026-03-20T14:00:00Z',
}

function renderDetailPage(id = sampleItem.itemId) {
  return renderWithProviders(
    <Routes>
      <Route path="/items/:id" element={<ItemDetailPage />} />
      <Route path="/" element={<div>Home</div>} />
    </Routes>,
    { route: `/items/${id}` },
  )
}

beforeEach(() => {
  vi.clearAllMocks()
  mockGetItem.mockResolvedValue(sampleItem)
})

describe('ItemDetailPage', () => {
  it('displays item name and notes', async () => {
    renderDetailPage()

    await waitFor(() => {
      expect(screen.getByText('Wireless Mouse')).toBeInTheDocument()
    })
    expect(screen.getByText('Ergonomic bluetooth mouse with silent clicks')).toBeInTheDocument()
  })

  it('shows loading state', () => {
    mockGetItem.mockReturnValue(new Promise(() => {}))
    renderDetailPage()
    expect(screen.getByText('Loading...')).toBeInTheDocument()
  })

  it('shows error state on fetch failure', async () => {
    mockGetItem.mockRejectedValue(new Error('Not found'))
    renderDetailPage()

    await waitFor(() => {
      expect(screen.getByText('Item not found or an error occurred.')).toBeInTheDocument()
    })
  })

  it('has a back to search link', async () => {
    renderDetailPage()

    await waitFor(() => {
      expect(screen.getByText('Wireless Mouse')).toBeInTheDocument()
    })

    const backLink = screen.getByText(/Back to search/)
    expect(backLink).toHaveAttribute('href', '/')
  })

  it('enters edit mode and saves changes', async () => {
    const updatedItem = { ...sampleItem, itemName: 'Updated Mouse' }
    mockUpdateItem.mockResolvedValue(updatedItem)
    const user = userEvent.setup()

    renderDetailPage()

    await waitFor(() => {
      expect(screen.getByText('Wireless Mouse')).toBeInTheDocument()
    })

    await user.click(screen.getByText('Edit'))

    expect(screen.getByDisplayValue('Wireless Mouse')).toBeInTheDocument()
    expect(screen.getByDisplayValue('Ergonomic bluetooth mouse with silent clicks')).toBeInTheDocument()

    const nameInput = screen.getByDisplayValue('Wireless Mouse')
    await user.clear(nameInput)
    await user.type(nameInput, 'Updated Mouse')

    await user.click(screen.getByText('Save'))

    await waitFor(() => {
      expect(mockUpdateItem).toHaveBeenCalledWith(sampleItem.itemId, {
        itemName: 'Updated Mouse',
        notes: 'Ergonomic bluetooth mouse with silent clicks',
      })
    })
  })

  it('cancels editing without saving', async () => {
    const user = userEvent.setup()
    renderDetailPage()

    await waitFor(() => {
      expect(screen.getByText('Wireless Mouse')).toBeInTheDocument()
    })

    await user.click(screen.getByText('Edit'))
    expect(screen.getByText('Edit Item')).toBeInTheDocument()

    await user.click(screen.getByText('Cancel'))

    expect(screen.queryByText('Edit Item')).not.toBeInTheDocument()
    expect(screen.getByText('Wireless Mouse')).toBeInTheDocument()
    expect(mockUpdateItem).not.toHaveBeenCalled()
  })

  it('shows delete confirmation and deletes item', async () => {
    mockDeleteItem.mockResolvedValue(undefined)
    const user = userEvent.setup()

    renderDetailPage()

    await waitFor(() => {
      expect(screen.getByText('Wireless Mouse')).toBeInTheDocument()
    })

    await user.click(screen.getByText('Delete'))
    expect(screen.getByText('Confirm')).toBeInTheDocument()

    await user.click(screen.getByText('Confirm'))

    await waitFor(() => {
      expect(mockDeleteItem).toHaveBeenCalledWith(sampleItem.itemId)
    })
  })

  it('cancels delete confirmation', async () => {
    const user = userEvent.setup()
    renderDetailPage()

    await waitFor(() => {
      expect(screen.getByText('Wireless Mouse')).toBeInTheDocument()
    })

    await user.click(screen.getByText('Delete'))
    expect(screen.getByText('Confirm')).toBeInTheDocument()

    await user.click(screen.getAllByText('Cancel')[0])
    expect(screen.queryByText('Confirm')).not.toBeInTheDocument()
    expect(mockDeleteItem).not.toHaveBeenCalled()
  })
})
