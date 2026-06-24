import { useState, FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { authApi } from '../api/services'

export default function LoginPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const { login } = useAuth()
  const navigate = useNavigate()

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const user = await authApi.login(email, password)
      login(user)
      navigate('/')
    } catch {
      setError('Ungültige E-Mail oder Passwort.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={{
      minHeight: '100vh', display: 'flex', alignItems: 'center',
      justifyContent: 'center', background: '#1e293b',
    }}>
      <div style={{ width: '100%', maxWidth: 380 }}>
        {/* Header */}
        <div style={{ textAlign: 'center', marginBottom: 28, color: 'white' }}>
          <div style={{ fontSize: 32, marginBottom: 8 }}>🔧</div>
          <h1 style={{ fontSize: 22, fontWeight: 700 }}>Glauser Illnau AG</h1>
          <p style={{ color: '#94a3b8', fontSize: 14, marginTop: 4 }}>Auftragsverwaltung</p>
        </div>

        <div className="card">
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>E-Mail</label>
              <input
                type="email"
                value={email}
                onChange={e => setEmail(e.target.value)}
                placeholder="name@glauser-illnau.ch"
                required
                autoFocus
              />
            </div>
            <div className="form-group">
              <label>Passwort</label>
              <input
                type="password"
                value={password}
                onChange={e => setPassword(e.target.value)}
                placeholder="••••••••"
                required
              />
            </div>
            {error && <p className="error-msg" style={{ marginBottom: 12 }}>{error}</p>}
            <button type="submit" className="btn-primary" disabled={loading} style={{ width: '100%', padding: 10 }}>
              {loading ? 'Anmelden...' : 'Anmelden'}
            </button>
          </form>
        </div>

        <p style={{ textAlign: 'center', color: '#64748b', fontSize: 12, marginTop: 16 }}>
          Test: admin@glauser-illnau.ch / admin123
        </p>
      </div>
    </div>
  )
}
