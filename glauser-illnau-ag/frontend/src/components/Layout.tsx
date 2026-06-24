import { Outlet, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Layout() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <div style={{ display: 'flex', minHeight: '100vh' }}>
      {/* Sidebar */}
      <aside style={{
        width: 220, background: '#1e293b', color: 'white',
        display: 'flex', flexDirection: 'column', flexShrink: 0,
      }}>
        {/* Logo */}
        <div style={{ padding: '20px 16px', borderBottom: '1px solid #334155' }}>
          <div style={{ fontSize: 13, color: '#94a3b8', marginBottom: 2 }}>Glauser Illnau AG</div>
          <div style={{ fontSize: 16, fontWeight: 700 }}>Auftragsverwaltung</div>
        </div>

        {/* Nav */}
        <nav style={{ flex: 1, padding: '12px 8px' }}>
          {[
            { to: '/', label: '📊 Dashboard', exact: true },
            { to: '/work-orders', label: '📋 Aufträge' },
            { to: '/customers', label: '🏢 Kunden' },
          ].map(({ to, label, exact }) => (
            <NavLink
              key={to}
              to={to}
              end={exact}
              style={({ isActive }) => ({
                display: 'block', padding: '9px 12px', borderRadius: 6,
                color: isActive ? 'white' : '#94a3b8',
                background: isActive ? '#3b82f6' : 'transparent',
                textDecoration: 'none', fontSize: 14, fontWeight: 500,
                marginBottom: 2,
              })}
            >
              {label}
            </NavLink>
          ))}
        </nav>

        {/* User info */}
        <div style={{ padding: '12px 16px', borderTop: '1px solid #334155' }}>
          <div style={{ fontSize: 13, fontWeight: 600, marginBottom: 2 }}>{user?.fullName}</div>
          <div style={{ fontSize: 11, color: '#94a3b8', marginBottom: 10 }}>{user?.role}</div>
          <button
            onClick={handleLogout}
            style={{ width: '100%', background: '#334155', color: '#cbd5e1', fontSize: 13 }}
          >
            Abmelden
          </button>
        </div>
      </aside>

      {/* Main content */}
      <main style={{ flex: 1, overflow: 'auto' }}>
        <Outlet />
      </main>
    </div>
  )
}
