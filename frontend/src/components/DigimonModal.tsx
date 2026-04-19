import { useQuery } from '@tanstack/react-query'
import { getDigimon, digimonImageUrl } from '../api/digimonApi'

interface DigimonModalProps {
  digimonId: string
  onClose: () => void
}

export default function DigimonModal({ digimonId, onClose }: DigimonModalProps) {
  const { data: digimon, isLoading, isError } = useQuery({
    queryKey: ['digimon', digimonId],
    queryFn: () => getDigimon(digimonId),
  })

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/40"
      onClick={onClose}
    >
      <div
        className="mx-4 w-full max-w-lg max-h-[80vh] overflow-y-auto rounded-xl bg-white p-6 shadow-xl"
        onClick={(e) => e.stopPropagation()}
      >
        {isLoading && <p className="text-gray-500 text-sm">Loading...</p>}
        {isError && <p className="text-red-600 text-sm">Failed to load Digimon.</p>}

        {digimon && (
          <>
            <div className="flex items-start justify-between">
              <h2 className="text-lg font-bold text-gray-900">{digimon.name}</h2>
              <button
                onClick={onClose}
                className="ml-4 shrink-0 rounded p-1 text-gray-400 hover:bg-gray-100 hover:text-gray-600"
              >
                ✕
              </button>
            </div>

            {digimon.imageUrl && (
              <div className="mt-4 flex justify-center">
                <img
                  src={digimonImageUrl(digimon.imageUrl)}
                  alt={digimon.name}
                  className="max-h-48 rounded-lg object-contain"
                  onError={(e) => { (e.target as HTMLImageElement).style.display = 'none' }}
                />
              </div>
            )}

            <div className="mt-4 rounded-lg border border-gray-200 bg-gray-50 p-4">
              <h3 className="text-xs font-medium text-gray-500 uppercase tracking-wide mb-2">Description</h3>
              {digimon.description ? (
                <p className="text-gray-800 text-sm whitespace-pre-wrap">{digimon.description}</p>
              ) : (
                <p className="text-gray-400 text-sm italic">No description.</p>
              )}
            </div>

            {digimon.wikiUrl && (
              <a
                href={digimon.wikiUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="mt-3 inline-block text-xs text-blue-600 hover:underline"
              >
                View on Wikimon
              </a>
            )}
          </>
        )}
      </div>
    </div>
  )
}
