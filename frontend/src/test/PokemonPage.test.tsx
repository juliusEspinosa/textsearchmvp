import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { vi, describe, it, expect, beforeEach } from 'vitest'
import { renderWithProviders } from './testUtils'
import PokemonPage from '../pages/PokemonPage'

vi.mock('../api/pokemonApi', () => ({
  searchPokemon: vi.fn(),
  listPokemon: vi.fn(),
  getPokemon: vi.fn(),
}))

import { searchPokemon, listPokemon, getPokemon } from '../api/pokemonApi'

const mockSearchPokemon = vi.mocked(searchPokemon)
const mockListPokemon = vi.mocked(listPokemon)
const mockGetPokemon = vi.mocked(getPokemon)

const samplePokemon = {
  pokemonId: '123e4567-e89b-12d3-a456-426614174000',
  pokedexNumber: 25,
  name: 'Pikachu',
  type1: 'Electric',
  type2: null,
  total: 320,
  hp: 35,
  attack: 55,
  defense: 40,
  spAtk: 50,
  spDef: 50,
  speed: 90,
  generation: 1,
  legendary: false,
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
  content: [samplePokemon],
  totalElements: 1,
  totalPages: 1,
  number: 0,
  size: 20,
  first: true,
  last: true,
}

beforeEach(() => {
  vi.clearAllMocks()
  mockListPokemon.mockResolvedValue(singlePage)
})

describe('PokemonPage', () => {
  it('renders search input', () => {
    renderWithProviders(<PokemonPage />)
    expect(screen.getByPlaceholderText('Search Pokemon by name...')).toBeInTheDocument()
  })

  it('shows recent pokemon table when query is empty', async () => {
    renderWithProviders(<PokemonPage />)

    await waitFor(() => {
      expect(screen.getByText('All Pokemon')).toBeInTheDocument()
    })
    expect(screen.getByText('Pikachu')).toBeInTheDocument()
    expect(screen.getByText('Electric')).toBeInTheDocument()
    expect(screen.getByText('25')).toBeInTheDocument()
  })

  it('shows search results after typing', async () => {
    mockSearchPokemon.mockResolvedValue(singlePage)
    const user = userEvent.setup()

    renderWithProviders(<PokemonPage />)

    await user.type(screen.getByPlaceholderText('Search Pokemon by name...'), 'pikachu')

    await waitFor(() => {
      expect(screen.getByText('1 result found')).toBeInTheDocument()
    })
    expect(screen.getByText('Pikachu')).toBeInTheDocument()
  })

  it('shows no results message', async () => {
    mockSearchPokemon.mockResolvedValue(emptyPage)
    const user = userEvent.setup()

    renderWithProviders(<PokemonPage />)

    await user.type(screen.getByPlaceholderText('Search Pokemon by name...'), 'xyznothing')

    await waitFor(() => {
      expect(screen.getByText(/No Pokemon found/)).toBeInTheDocument()
    })
  })

  it('clicking name opens modal', async () => {
    mockGetPokemon.mockResolvedValue(samplePokemon)
    const user = userEvent.setup()

    renderWithProviders(<PokemonPage />)

    await waitFor(() => {
      expect(screen.getByText('Pikachu')).toBeInTheDocument()
    })

    await user.click(screen.getByText('Pikachu'))

    await waitFor(() => {
      expect(screen.getByText('#25 Pikachu')).toBeInTheDocument()
    })
    expect(screen.getByText('Not legendary')).toBeInTheDocument()
  })
})
