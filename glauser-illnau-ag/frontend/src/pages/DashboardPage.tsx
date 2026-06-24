import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { workOrderApi } from '../api/services'
import { useAuth } from '../context/AuthContext'
import type { WorkOrder, OrderStatus } from '../types'
import { STATUS_LABELS, STATUS_COLORS } from '../types'
import StatusBadge from '../components/StatusBadge'

const ALL_STATUSES: OrderStatus[] = ['CREATED', 'SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'APPROVED', 'INVOICED']

export default function DashboardPage() {
  const [orders, setOrders] = useState<WorkOrder[]>([])
  const [loading, setLoading] = useState(true)
  const { user } = useAuth()
  const navigate = useNavigate()

  useEffect(() => {
    workOrderApi.getAll().then(setOrders).finally(() => setLoading(false))
  }, [])

  const countByStatus = (s: OrderStatus) => orders.filter(o => o.status === s).length
  const recent = [...orders]
    .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
    .slice(0, 8)

  const pendingActions = orders.filter(o =>
    o.status === 'CREATED' || o.status === 'COMPLETED'
  ).length

  return (
    <div style={{ padding: 28 }}>
      {/* Welcome */}
      <div style={{ marginBottom: 24 }}>
        <h1 style={{ fontSize: 22, fontWeight: 700 }}>Guten Tag, {user?.fullName}</h1>
        <p style={{ color: 'var(--gray-500)', marginTop: 2 }}>
          {pendingActions > 0
            ? `${pendingActions} Auftrag${pendingActions > 1 ? 'träge' : ''} benötigen Ihre Aufmerksamkeit.`
            : 'Alle Aufträge sind auf dem neuesten Stand.'}
        </p>
      </div>

      {/* Status overview */}
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fill, minmax(140px, 1fr))',
        gap: 12,
        marginBottom: 28,
      }}>
        {ALL_STATUSES.map(status => (
          <div
            key={status}
            className="card"
            style={{
              cursor: 'pointer',
              borderLeft: `4px solid ${STATUS_COLORS[status]}`,
              padding: '14px 16px',
              transition: 'box-shadow 0.15s',
            }}
            onClick={() => navigate(`/work-orders?status=${status}`)}
          >
            <div style={{ fontSize: 26, fontWeight: 700, color: STATUS_COLORS[status] }}>
              {loading ? '–' : countByStatus(status)}
            </div>
            <div style={{ fontSize: 12, color: 'var(--gray-500)', marginTop: 3, lineHeight: 1.3 }}>
              {STATUS_LABELS[status]}
            </div>
          </div>
        ))}
      </div>

      {/* Total */}
      <div style={{ display: 'flex', gap: 12, marginBottom: 28 }}>
        <div className="card" style={{ padding: '14px 20px', display: 'flex', alignItems: 'center', gap: 16 }}>
          <div style={{ fontSize: 28, fontWeight: 700 }}>{loading ? '–' : orders.length}</div>
          <div style={{ fontSize: 13, color: 'var(--gray-500)' }}>Aufträge total</div>
        </div>
        <div className="card" style={{ padding: '14px 20px', display: 'flex', alignItems: 'center', gap: 16 }}>
          <div style={{ fontSize: 28, fontWeight: 700, color: 'var(--success)' }}>
            {loading ? '–' : countByStatus('INVOICED')}
          </div>
          <div style={{ fontSize: 13, color: 'var(--gray-500)' }}>Verrechnet</div>
        </div>
      </div>

      {/* Recent orders */}
      <div className="card">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
          <h2 style={{ fontSize: 16, fontWeight: 600 }}>Neueste Aufträge</h2>
          <button className="btn-primary btn-sm" onClick={() => navigate('/work-orders')}>
            Alle anzeigen →
          </button>
        </div>
        {loading ? (
          <p style={{ color: 'var(--gray-500)', padding: '16px 0' }}>Lädt...</p>
        ) : recent.length === 0 ? (
          <div style={{ textAlign: 'center', padding: 32, color: 'var(--gray-500)' }}>
            <div style={{ fontSize: 32, marginBottom: 8 }}>📋</div>
            Noch keine Aufträge vorhanden.
            <div style={{ marginTop: 12 }}>
              <button className="btn-primary btn-sm" onClick={() => navigate('/work-orders')}>
                Ersten Auftrag erfassen
              </button>
            </div>
          </div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Titel</th>
                <th>Kunde</th>
                <th>Mitarbeiter</th>
                <th>Status</th>
                <th>Erstellt</th>
              </tr>
            </thead>
            <tbody>
              {recent.map(order => (
                <tr
                  key={order.id}
                  style={{ cursor: 'pointer' }}
                  onClick={() => navigate(`/work-orders/${order.id}`)}
                >
                  <td style={{ color: 'var(--gray-500)', fontWeight: 500 }}>#{order.id}</td>
                  <td style={{ fontWeight: 500 }}>{order.title}</td>
                  <td>{order.customerName}</td>
                  <td style={{ color: order.employeeName ? 'inherit' : 'var(--gray-300)' }}>
                    {order.employeeName ?? '–'}
                  </td>
                  <td><StatusBadge status={order.status} /></td>
                  <td style={{ color: 'var(--gray-500)' }}>
                    {new Date(order.createdAt).toLocaleDateString('de-CH')}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  )
}
