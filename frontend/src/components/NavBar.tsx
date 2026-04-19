import { Link } from 'react-router-dom'
import HealthBadge from './HealthBadge'

export default function NavBar() {
  return (
    <nav className="border-b border-gray-200 bg-white">
      <div className="mx-auto flex max-w-4xl items-center justify-between px-4 py-3">
        <div className="flex items-center gap-6">
          <Link to="/" className="text-lg font-semibold text-gray-900 no-underline">
            Item Notes Search
          </Link>
          <Link to="/purchases" className="text-sm text-gray-600 hover:text-gray-900 no-underline">
            Purchases
          </Link>
          <Link to="/pokemon" className="text-sm text-gray-600 hover:text-gray-900 no-underline">
            Pokemon
          </Link>
          <Link to="/digimon" className="text-sm text-gray-600 hover:text-gray-900 no-underline">
            Digimon
          </Link>
        </div>
        <HealthBadge />
      </div>
    </nav>
  )
}
