import { useEffect, useState } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { workOrderApi, customerApi } from '../api/services'
import { useAuth } from '../context/AuthContext'
import type { WorkOrder, Customer, OrderStatus } from '../types'
import { STATUS_LABELS } from '../types'
import StatusBadge from '../components/StatusBadge'

const ALL_STATUSES: OrderStatus[] = ['CREATED', 'SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'APPROVED', 'INVOICED']

interface CreateForm {
  title: string
  description: string
  address: string
  customerId: string
}

export default function WorkOrdersPage() {
  const [orders, setOrders] = useState<WorkOrder[]>([])
  const [customers, setCustomers] = useState<Customer[]>([])
  const [loading, setLoading] = useState(true)
  const [showCreate, setShowCreate] = useState(false)
  const [form, setForm] = useState<CreateForm>({ title: '', description: '', address: '', customerId: '' })
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  const [searchParams, setSearchParams] = useSearchParams()
  const statusFilter = searchParams.get('status') as OrderStatus | null
  const navigate = useNavigate()
  const { isManager } = useAuth()

  const load = () => {
    setLoading(true)
    workOrderApi.getAll(statusFilter ?? undefined)
      .then(setOrders)
      .finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [statusFilter])
  useEffect(() => { customerApi.getAll().then(setCustomers) }, [])

  const handleCreate = async () => {
    if (!form.title.trim() || !form.customerId) {
      setError('Titel und Kunde sind pflicht.')
      return
    }
    setSaving(true)
    setError('')
    try {
      await workOrderApi.create({
        title: form.title,
        description: form.description,
        address: form.address,
        customerId: Number(form.customerId),
      })
      setShowCreate(false)
      setForm({ title: '', description: '', address: '', customerId: '' })
      load()
    } catch {
      setError('Fehler beim Erstellen.')
    } finally {
      setSaving(false)
    }
  }

  return (
    <div style={{ padding: 28 }}>
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h1 style={{ fontSize: 22, fontWeight: 700 }}>Aufträge</h1>
        {isManager && (
          <button className="btn-primary" onClick={() => setShowCreate(true)}>
            + Auftrag erfassen
          </button>
        )}
      </div>

      {/* Filter tabs */}
      <div style={{ display: 'flex', gap: 6, marginBottom: 20, flexWrap: 'wrap' }}>
        <button
          className={statusFilter === null ? 'btn-primary btn-sm' : 'btn-secondary btn-sm'}
          onClick={() => setSearchParams({})}
        >
          Alle
        </button>
        {ALL_STATUSES.map(s => (
          <button
            key={s}
            className={statusFilter === s ? 'btn-primary btn-sm' : 'btn-secondary btn-sm'}
            onClick={() => setSearchParams({ status: s })}
          >
            {STATUS_LABELS[s]}
          </button>
        ))}
      </div>

      {/* Table */}
      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        {loading ? (
          <p style={{ padding: 24, color: 'var(--gray-500)' }}>Lädt...</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Titel</th>
                <th>Kunde</th>
                <th>Mitarbeiter</th>
                <th>Status</th>
                <th>Termin</th>
                <th>Erstellt</th>
              </tr>
            </thead>
            <tbody>
              {orders.map(order => (
                <tr
                  key={order.id}
                  style={{ cursor: 'pointer' }}
                  onClick={() => navigate(`/work-orders/${order.id}`)}
                >
                  <td style={{ color: 'var(--gray-500)', fontWeight: 500 }}>#{order.id}</td>
                  <td style={{ fontWeight: 500 }}>{order.title}</td>
                  <td>{order.customerName}</td>
                  <td>{order.employeeName ?? <span style={{ color: 'var(--gray-300)' }}>–</span>}</td>
                  <td><StatusBadge status={order.status} /></td>
                  <td style={{ color: 'var(--gray-500)' }}>
                    {order.scheduledAt ? new Date(order.scheduledAt).toLocaleDateString('de-CH') : '–'}
                  </td>
                  <td style={{ color: 'var(--gray-500)' }}>
                    {new Date(order.createdAt).toLocaleDateString('de-CH')}
                  </td>
                </tr>
              ))}
              {orders.length === 0 && (
                <tr>
                  <td colSpan={7} style={{ textAlign: 'center', padding: 32, color: 'var(--gray-500)' }}>
                    Keine Aufträge gefunden
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        )}
      </div>

      {/* Create Modal */}
      {showCreate && (
        <div className="modal-overlay" onClick={() => setShowCreate(false)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Auftrag erfassen</h2>
              <button className="close-btn" onClick={() => setShowCreate(false)}>×</button>
            </div>
            <div className="modal-body">
              <div className="form-group">
                <label>Titel *</label>
                <input
                  value={form.title}
                  onChange={e => setForm(f => ({ ...f, title: e.target.value }))}
                  placeholder="z.B. Heizungsservice"
                  autoFocus
                />
              </div>
              <div className="form-group">
                <label>Kunde *</label>
                <select
                  value={form.customerId}
                  onChange={e => setForm(f => ({ ...f, customerId: e.target.value }))}
                >
                  <option value="">– Kunde wählen –</option>
                  {customers.map(c => (
                    <option key={c.id} value={c.id}>{c.companyName}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>Adresse</label>
                <input
                  value={form.address}
                  onChange={e => setForm(f => ({ ...f, address: e.target.value }))}
                  placeholder="Ausführungsadresse"
                />
              </div>
              <div className="form-group">
                <label>Beschreibung</label>
                <textarea
                  value={form.description}
                  onChange={e => setForm(f => ({ ...f, description: e.target.value }))}
                  placeholder="Details zum Auftrag..."
                />
              </div>
              {error && <p className="error-msg">{error}</p>}
            </div>
            <div className="modal-footer">
              <button className="btn-secondary" onClick={() => setShowCreate(false)}>Abbrechen</button>
              <button className="btn-primary" onClick={handleCreate} disabled={saving}>
                {saving ? 'Speichern...' : 'Auftrag erstellen'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
