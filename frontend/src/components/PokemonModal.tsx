import { useQuery } from '@tanstack/react-query'
import { getPokemon } from '../api/pokemonApi'

interface PokemonModalProps {
  pokemonId: string
  onClose: () => void
}

export default function PokemonModal({ pokemonId, onClose }: PokemonModalProps) {
  const { data: pokemon, isLoading, isError } = useQuery({
    queryKey: ['pokemon', pokemonId],
    queryFn: () => getPokemon(pokemonId),
  })

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

        {isError && <p className="text-red-600 text-sm">Failed to load Pokemon.</p>}

        {pokemon && (
          <>
            <div className="flex items-start justify-between">
              <h2 className="text-lg font-bold text-gray-900">
                #{pokemon.pokedexNumber} {pokemon.name}
              </h2>
              <button
                onClick={onClose}
                className="ml-4 shrink-0 rounded p-1 text-gray-400 hover:bg-gray-100 hover:text-gray-600"
              >
                ✕
              </button>
            </div>

            <div className="mt-3 text-sm text-gray-600">
              <span className="font-medium">Type: </span>
              {pokemon.type1}{pokemon.type2 ? ` / ${pokemon.type2}` : ''}
            </div>

            <div className="mt-4 rounded-lg border border-gray-200 bg-gray-50 p-4">
              <h3 className="text-xs font-medium text-gray-500 uppercase tracking-wide mb-3">Stats</h3>
              <div className="grid grid-cols-2 gap-2 text-sm">
                <div className="flex justify-between">
                  <span className="text-gray-500">HP</span>
                  <span className="font-medium text-gray-900">{pokemon.hp}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-500">Attack</span>
                  <span className="font-medium text-gray-900">{pokemon.attack}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-500">Defense</span>
                  <span className="font-medium text-gray-900">{pokemon.defense}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-500">Sp. Atk</span>
                  <span className="font-medium text-gray-900">{pokemon.spAtk}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-500">Sp. Def</span>
                  <span className="font-medium text-gray-900">{pokemon.spDef}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-500">Speed</span>
                  <span className="font-medium text-gray-900">{pokemon.speed}</span>
                </div>
              </div>
              <div className="mt-3 pt-3 border-t border-gray-200 flex justify-between text-sm">
                <span className="text-gray-500">Total</span>
                <span className="font-bold text-gray-900">{pokemon.total}</span>
              </div>
            </div>

            <div className="mt-3 flex gap-4 text-xs text-gray-400">
              <span>Generation {pokemon.generation}</span>
              <span>{pokemon.legendary ? 'Legendary' : 'Not legendary'}</span>
            </div>
          </>
        )}
      </div>
    </div>
  )
}
