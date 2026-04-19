import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { vi, describe, it, expect, beforeEach } from 'vitest'
import { renderWithProviders } from './testUtils'
import SearchPage from '../pages/SearchPage'

vi.mock('../api/itemsApi', () => ({
  searchItems: vi.fn(),
  listItems: vi.fn(),
}))

import { searchItems, listItems } from '../api/itemsApi'

const mockSearchItems = vi.mocked(searchItems)
const mockListItems = vi.mocked(listItems)

const sampleItem = {
  itemId: '123e4567-e89b-12d3-a456-426614174000',
  itemName: 'Wireless Mouse',
  notes: 'Ergonomic bluetooth mouse',
  createdAt: '2026-01-01T00:00:00Z',
  updatedAt: '2026-01-01T00:00:00Z',
}

const emptyPage = {
  content: [],
  totalElements: 0,
  totalPages: 0,
  number: 0,
  size: 20,
  first: true,
  last: true,
  searchDurationMs: 2,
}

const singlePage = {
  content: [sampleItem],
  totalElements: 1,
  totalPages: 1,
  number: 0,
  size: 20,
  first: true,
  last: true,
  searchDurationMs: 3,
}

beforeEach(() => {
  vi.clearAllMocks()
  mockListItems.mockResolvedValue(singlePage)
})

describe('SearchPage', () => {
  it('renders search input', () => {
    renderWithProviders(<SearchPage />)
    expect(screen.getByPlaceholderText('Search by name or notes...')).toBeInTheDocument()
  })

  it('shows recent items when query is empty', async () => {
    renderWithProviders(<SearchPage />)

    await waitFor(() => {
      expect(screen.getByText('Recent items')).toBeInTheDocument()
    })
    expect(screen.getByText('Wireless Mouse')).toBeInTheDocument()
  })

  it('shows search results after typing', async () => {
    mockSearchItems.mockResolvedValue(singlePage)
    const user = userEvent.setup()

    renderWithProviders(<SearchPage />)

    await user.type(screen.getByPlaceholderText('Search by name or notes...'), 'mouse')

    await waitFor(() => {
      expect(screen.getByText('1 result found')).toBeInTheDocument()
    })
    expect(screen.getByText('Wireless Mouse')).toBeInTheDocument()
  })

  it('shows no results message when search returns empty', async () => {
    mockSearchItems.mockResolvedValue(emptyPage)
    const user = userEvent.setup()

    renderWithProviders(<SearchPage />)

    await user.type(screen.getByPlaceholderText('Search by name or notes...'), 'xyznothing')

    await waitFor(() => {
      expect(screen.getByText(/No items found/)).toBeInTheDocument()
    })
  })

  it('shows error message on search failure', async () => {
    mockSearchItems.mockRejectedValue(new Error('Network error'))
    const user = userEvent.setup()

    renderWithProviders(<SearchPage />)

    await user.type(screen.getByPlaceholderText('Search by name or notes...'), 'fail')

    await waitFor(() => {
      expect(screen.getByText('Something went wrong. Please try again.')).toBeInTheDocument()
    })
  })

  it('links search results to item detail page', async () => {
    mockSearchItems.mockResolvedValue(singlePage)
    const user = userEvent.setup()

    renderWithProviders(<SearchPage />)

    await user.type(screen.getByPlaceholderText('Search by name or notes...'), 'mouse')

    await waitFor(() => {
      expect(screen.getByText('Wireless Mouse')).toBeInTheDocument()
    })

    const link = screen.getByText('Wireless Mouse').closest('a')
    expect(link).toHaveAttribute('href', `/items/${sampleItem.itemId}`)
  })
})
