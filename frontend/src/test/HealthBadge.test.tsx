import { screen, waitFor } from '@testing-library/react'
import { vi, describe, it, expect, beforeEach } from 'vitest'
import { renderWithProviders } from './testUtils'
import HealthBadge from '../components/HealthBadge'

vi.mock('../api/healthApi', () => ({
  checkHealth: vi.fn(),
}))

import { checkHealth } from '../api/healthApi'

const mockCheckHealth = vi.mocked(checkHealth)

beforeEach(() => {
  vi.clearAllMocks()
})

describe('HealthBadge', () => {
  it('shows "Backend UP" when health check succeeds', async () => {
    mockCheckHealth.mockResolvedValue({ status: 'UP' })
    renderWithProviders(<HealthBadge />)

    await waitFor(() => {
      expect(screen.getByText('Backend UP')).toBeInTheDocument()
    })
  })

  it('shows "Backend DOWN" when health check fails', async () => {
    mockCheckHealth.mockRejectedValue(new Error('Connection refused'))
    renderWithProviders(<HealthBadge />)

    await waitFor(
      () => {
        expect(screen.getByText('Backend DOWN')).toBeInTheDocument()
      },
      { timeout: 5000 },
    )
  })

  it('shows "Checking..." while loading', () => {
    mockCheckHealth.mockReturnValue(new Promise(() => {}))
    renderWithProviders(<HealthBadge />)
    expect(screen.getByText('Checking...')).toBeInTheDocument()
  })
})
