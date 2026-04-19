import { useState } from 'react'
import { usePokemonSearch } from '../hooks/usePokemonSearch'
import { useRecentPokemon } from '../hooks/useRecentPokemon'
import { useDebouncedValue } from '../hooks/useDebouncedValue'
import PokemonModal from '../components/PokemonModal'
import Pagination from '../components/Pagination'
import type { PokemonResponse } from '../api/pokemonApi'
import DOMPurify from 'dompurify'

function PokemonTable({
  pokemon,
  onNameClick,
}: {
  pokemon: PokemonResponse[]
  onNameClick: (pokemonId: string) => void
}) {
  return (
    <table className="w-full border border-gray-200 rounded-lg bg-white text-sm">
      <thead>
        <tr className="bg-gray-50 text-left text-gray-600">
          <th className="px-4 py-3 font-medium">#</th>
          <th className="px-4 py-3 font-medium">Name</th>
          <th className="px-4 py-3 font-medium">Type</th>
          <th className="px-4 py-3 font-medium text-right">Total</th>
          <th className="px-4 py-3 font-medium text-right">HP</th>
          <th className="px-4 py-3 font-medium text-right">Atk</th>
          <th className="px-4 py-3 font-medium text-right">Def</th>
          <th className="px-4 py-3 font-medium text-right">SpA</th>
          <th className="px-4 py-3 font-medium text-right">SpD</th>
          <th className="px-4 py-3 font-medium text-right">Spe</th>
          <th className="px-4 py-3 font-medium text-right">Gen</th>
          <th className="px-4 py-3 font-medium">Legendary</th>
        </tr>
      </thead>
      <tbody className="divide-y divide-gray-200">
        {pokemon.map((p) => (
          <tr key={p.pokemonId} className="hover:bg-gray-50">
            <td className="px-4 py-3 text-gray-900">{p.pokedexNumber}</td>
            <td className="px-4 py-3">
              <button
                onClick={() => onNameClick(p.pokemonId)}
                className="text-blue-600 hover:text-blue-800 hover:underline cursor-pointer bg-transparent border-none p-0 text-sm text-left"
              >
                {p.name}
              </button>
            </td>
            <td className="px-4 py-3 text-gray-900">
              {p.type1}{p.type2 ? ` / ${p.type2}` : ''}
            </td>
            <td className="px-4 py-3 text-right text-gray-900">{p.total}</td>
            <td className="px-4 py-3 text-right text-gray-900">{p.hp}</td>
            <td className="px-4 py-3 text-right text-gray-900">{p.attack}</td>
            <td className="px-4 py-3 text-right text-gray-900">{p.defense}</td>
            <td className="px-4 py-3 text-right text-gray-900">{p.spAtk}</td>
            <td className="px-4 py-3 text-right text-gray-900">{p.spDef}</td>
            <td className="px-4 py-3 text-right text-gray-900">{p.speed}</td>
            <td className="px-4 py-3 text-right text-gray-900">{p.generation}</td>
            <td className="px-4 py-3 text-gray-900">{p.legendary ? 'Yes' : 'No'}</td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}

export default function PokemonPage() {
  const [query, setQuery] = useState('')
  const [page, setPage] = useState(0)
  const [selectedPokemonId, setSelectedPokemonId] = useState<string | null>(null)
  const debouncedQuery = useDebouncedValue(query, 300)
  const { data, isLoading, isError, isFetching } = usePokemonSearch(debouncedQuery, page)
  const { data: recentData } = useRecentPokemon(page)

  const isSearching = debouncedQuery.trim().length > 0
  const activeData = isSearching ? data : recentData

  function handleQueryChange(value: string) {
    setQuery(value)
    setPage(0)
  }

  return (
    <div className="mx-auto max-w-6xl px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Pokemon</h1>

      <div className="relative">
        <input
          type="text"
          placeholder="Search Pokemon by name..."
          value={query}
          onChange={(e) => handleQueryChange(e.target.value)}
          className="w-full rounded-lg border border-gray-300 px-4 py-3 text-gray-900 placeholder-gray-400 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-200"
        />
        {isFetching && (
          <span className="absolute right-3 top-3.5 text-xs text-gray-400">searching...</span>
        )}
      </div>

      <div className="mt-6">
        {!isSearching && recentData && recentData.content.length > 0 && (
          <>
            <p className="text-gray-500 text-xs mb-3 uppercase tracking-wide font-medium">
              All Pokemon
            </p>
            <PokemonTable pokemon={recentData.content} onNameClick={setSelectedPokemonId} />
          </>
        )}

        {!isSearching && !recentData && (
          <p className="text-gray-500 text-sm">Type to search Pokemon by name.</p>
        )}

        {isSearching && isLoading && (
          <p className="text-gray-500 text-sm">Searching...</p>
        )}

        {isSearching && isError && (
          <p className="text-red-600 text-sm">Something went wrong. Please try again.</p>
        )}

        {isSearching && data && data.content.length === 0 && (
          <p className="text-gray-500 text-sm">No Pokemon found for &ldquo;{DOMPurify.sanitize(debouncedQuery)}&rdquo;.</p>
        )}

        {isSearching && data && data.content.length > 0 && (
          <>
            <p className="text-gray-500 text-xs mb-3">
              {data.totalElements} result{data.totalElements !== 1 ? 's' : ''} found
            </p>
            <PokemonTable pokemon={data.content} onNameClick={setSelectedPokemonId} />
          </>
        )}

        {activeData && activeData.content.length > 0 && (
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

      {selectedPokemonId && (
        <PokemonModal pokemonId={selectedPokemonId} onClose={() => setSelectedPokemonId(null)} />
      )}
    </div>
  )
}
