import { useHealthCheck } from '../hooks/useHealthCheck'

export default function HealthBadge() {
  const { data, isError, isLoading } = useHealthCheck()

  const isUp = data?.status === 'UP'

  return (
    <span className="inline-flex items-center gap-1.5 text-sm">
      <span
        className={`inline-block h-2.5 w-2.5 rounded-full ${
          isLoading
            ? 'bg-gray-400 animate-pulse'
            : isUp
              ? 'bg-green-500'
              : 'bg-red-500'
        }`}
      />
      <span className="text-gray-600">
        {isLoading ? 'Checking...' : isUp ? 'Backend UP' : isError ? 'Backend DOWN' : 'Backend DOWN'}
      </span>
    </span>
  )
}
