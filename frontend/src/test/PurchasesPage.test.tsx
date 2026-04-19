import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { vi, describe, it, expect, beforeEach } from 'vitest'
import { renderWithProviders } from './testUtils'
import PurchasesPage from '../pages/PurchasesPage'

vi.mock('../api/purchasesApi', () => ({
  searchPurchases: vi.fn(),
  listPurchases: vi.fn(),
}))

vi.mock('../api/itemsApi', () => ({
  getItem: vi.fn(),
}))

import { searchPurchases, listPurchases } from '../api/purchasesApi'
import { getItem } from '../api/itemsApi'

const mockSearchPurchases = vi.mocked(searchPurchases)
const mockListPurchases = vi.mocked(listPurchases)
const mockGetItem = vi.mocked(getItem)

const samplePurchase = {
  purchaseId: '223e4567-e89b-12d3-a456-426614174000',
  buyer: 'John Smith',
  itemId: '123e4567-e89b-12d3-a456-426614174000',
  itemName: 'Wireless Mouse',
  quantity: 3,
  purchasedAt: '2026-01-01T00:00:00Z',
}

const emptyPage = {
  content: [],
  totalElements: 0,
  totalPages: 0,
  number: 0,
  size: 20,
  first: true,
  last: true,
}

const singlePage = {
  content: [samplePurchase],
  totalElements: 1,
  totalPages: 1,
  number: 0,
  size: 20,
  first: true,
  last: true,
}

beforeEach(() => {
  vi.clearAllMocks()
  mockListPurchases.mockResolvedValue(singlePage)
})

describe('PurchasesPage', () => {
  it('renders search input', () => {
    renderWithProviders(<PurchasesPage />)
    expect(screen.getByPlaceholderText('Search by item name or notes...')).toBeInTheDocument()
  })

  it('shows recent purchases table when query is empty', async () => {
    renderWithProviders(<PurchasesPage />)

    await waitFor(() => {
      expect(screen.getByText('Recent purchases')).toBeInTheDocument()
    })
    expect(screen.getByText('John Smith')).toBeInTheDocument()
    expect(screen.getByText('Wireless Mouse')).toBeInTheDocument()
    expect(screen.getByText('3')).toBeInTheDocument()
  })

  it('shows search results after typing', async () => {
    mockSearchPurchases.mockResolvedValue(singlePage)
    const user = userEvent.setup()

    renderWithProviders(<PurchasesPage />)

    await user.type(screen.getByPlaceholderText('Search by item name or notes...'), 'John')

    await waitFor(() => {
      expect(screen.getByText('1 result found')).toBeInTheDocument()
    })
    expect(screen.getByText('John Smith')).toBeInTheDocument()
  })

  it('shows no results message', async () => {
    mockSearchPurchases.mockResolvedValue(emptyPage)
    const user = userEvent.setup()

    renderWithProviders(<PurchasesPage />)

    await user.type(screen.getByPlaceholderText('Search by item name or notes...'), 'xyznothing')

    await waitFor(() => {
      expect(screen.getByText(/No purchases found/)).toBeInTheDocument()
    })
  })

  it('clicking item name opens notes modal', async () => {
    mockGetItem.mockResolvedValue({
      itemId: samplePurchase.itemId,
      itemName: 'Wireless Mouse',
      notes: 'Ergonomic bluetooth mouse',
      createdAt: '2026-01-01T00:00:00Z',
      updatedAt: '2026-01-01T00:00:00Z',
    })
    const user = userEvent.setup()

    renderWithProviders(<PurchasesPage />)

    await waitFor(() => {
      expect(screen.getByText('Wireless Mouse')).toBeInTheDocument()
    })

    await user.click(screen.getByText('Wireless Mouse'))

    await waitFor(() => {
      expect(screen.getByText('Ergonomic bluetooth mouse')).toBeInTheDocument()
    })
  })
})
