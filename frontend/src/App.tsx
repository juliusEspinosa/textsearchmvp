import { Routes, Route } from 'react-router-dom'
import NavBar from './components/NavBar'
import SearchPage from './pages/SearchPage'
import ItemDetailPage from './pages/ItemDetailPage'
import PurchasesPage from './pages/PurchasesPage'
import PokemonPage from './pages/PokemonPage'
import DigimonPage from './pages/DigimonPage'

export default function App() {
  return (
    <div className="min-h-screen bg-gray-50">
      <NavBar />
      <Routes>
        <Route path="/" element={<SearchPage />} />
        <Route path="/items/:id" element={<ItemDetailPage />} />
        <Route path="/purchases" element={<PurchasesPage />} />
        <Route path="/pokemon" element={<PokemonPage />} />
        <Route path="/digimon" element={<DigimonPage />} />
      </Routes>
    </div>
  )
}
