import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { workOrderApi, employeeApi, reportApi } from '../api/services'
import { useAuth } from '../context/AuthContext'
import type { WorkOrder, Employee, Report } from '../types'
import StatusBadge from '../components/StatusBadge'

export default function WorkOrderDetailPage() {
  const { id } = useParams<{ id: string }>()
  const orderId = Number(id)
  const navigate = useNavigate()
  const { isManager, isAdmin } = useAuth()

  const [order, setOrder] = useState<WorkOrder | null>(null)
  const [employees, setEmployees] = useState<Employee[]>([])
  const [report, setReport] = useState<Report | null>(null)
  const [loading, setLoading] = useState(true)
  const [actionLoading, setActionLoading] = useState(false)
  const [error, setError] = useState('')

  // Dispatch modal state
  const [showDispatch, setShowDispatch] = useState(false)
  const [selectedEmployee, setSelectedEmployee] = useState('')
  const [scheduledAt, setScheduledAt] = useState('')

  // Report modal state
  const [showReport, setShowReport] = useState(false)
  const [reportForm, setReportForm] = useState({
    workDescription: '',
    workingHours: '',
    usedMaterials: '',
  })
  const [reportError, setReportError] = useState('')

  const loadOrder = async () => {
    setLoading(true)
    try {
      const o = await workOrderApi.getById(orderId)
      setOrder(o)
      // Try to load report (404 = no report yet, that's fine)
      try {
        const r = await reportApi.get(orderId)
        setReport(r)
      } catch {
        setReport(null)
      }
    } catch {
      setError('Auftrag konnte nicht geladen werden.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadOrder()
    employeeApi.getAll().then(setEmployees).catch(() => {})
  }, [orderId])

  const doAction = async (action: () => Promise<WorkOrder>) => {
    setActionLoading(true)
    setError('')
    try {
      const updated = await action()
      setOrder(updated)
    } catch (e: any) {
      setError(e.response?.data?.error ?? 'Fehler bei der Aktion')
    } finally {
      setActionLoading(false)
    }
  }

  const handleDispatch = async () => {
    if (!selectedEmployee) return
    await doAction(() =>
      workOrderApi.dispatch(orderId, Number(selectedEmployee), scheduledAt || undefined)
    )
    setShowDispatch(false)
    setSelectedEmployee('')
    setScheduledAt('')
  }

  const handleCreateReport = async () => {
    if (!reportForm.workDescription.trim() || !reportForm.workingHours) {
      setReportError('Beschreibung und Arbeitsstunden sind pflicht.')
      return
    }
    setActionLoading(true)
    setReportError('')
    try {
      // POST /api/work-orders/{id}/report → backend auto-sets order to COMPLETED
      const r = await reportApi.create(orderId, {
        workDescription: reportForm.workDescription,
        workingHours: Number(reportForm.workingHours),
        usedMaterials: reportForm.usedMaterials || undefined,
      })
      setReport(r)
      // Reload order to get updated status (COMPLETED)
      const updated = await workOrderApi.getById(orderId)
      setOrder(updated)
      setShowReport(false)
      setReportForm({ workDescription: '', workingHours: '', usedMaterials: '' })
    } catch (e: any) {
      setReportError(e.response?.data?.error ?? 'Fehler beim Speichern des Rapports')
    } finally {
      setActionLoading(false)
    }
  }

  if (loading) {
    return (
      <div style={{ padding: 28, color: 'var(--gray-500)' }}>Laden...</div>
    )
  }
  if (!order) {
    return (
      <div style={{ padding: 28 }}>
        <p style={{ color: 'var(--danger)' }}>Auftrag nicht gefunden.</p>
        <button className="btn-secondary" style={{ marginTop: 12 }} onClick={() => navigate('/work-orders')}>
          Zurück zur Liste
        </button>
      </div>
    )
  }

  const fmt = (d?: string) =>
    d ? new Date(d).toLocaleString('de-CH', { dateStyle: 'short', timeStyle: 'short' }) : '–'

  return (
    <div style={{ padding: 28, maxWidth: 900 }}>
      {/* Back */}
      <button className="btn-secondary btn-sm" onClick={() => navigate('/work-orders')} style={{ marginBottom: 16 }}>
        ← Zurück zur Liste
      </button>

      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 20 }}>
        <div>
          <h1 style={{ fontSize: 22, fontWeight: 700, marginBottom: 8 }}>{order.title}</h1>
          <StatusBadge status={order.status} />
        </div>
        <div style={{ fontSize: 13, color: 'var(--gray-500)', textAlign: 'right' }}>
          <div>Auftrag #{order.id}</div>
          <div style={{ marginTop: 2 }}>Erstellt: {fmt(order.createdAt)}</div>
        </div>
      </div>

      {/* Error banner */}
      {error && (
        <div style={{
          background: 'var(--danger-light)', color: 'var(--danger)',
          padding: '10px 14px', borderRadius: 6, marginBottom: 16, fontSize: 14,
        }}>
          ⚠️ {error}
        </div>
      )}

      {/* Info cards */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16, marginBottom: 20 }}>
        <div className="card">
          <SectionTitle>Auftragsdaten</SectionTitle>
          <InfoRow label="Kunde" value={order.customerName} />
          <InfoRow label="Adresse" value={order.address || '–'} />
          <InfoRow label="Beschreibung" value={order.description || '–'} />
        </div>
        <div className="card">
          <SectionTitle>Disposition & Termine</SectionTitle>
          <InfoRow label="Mitarbeiter" value={order.employeeName ?? '–'} />
          <InfoRow label="Geplanter Termin" value={fmt(order.scheduledAt)} />
          <InfoRow label="Abgeschlossen am" value={fmt(order.completedAt)} />
          <InfoRow label="Verrechnet am" value={fmt(order.invoicedAt)} />
        </div>
      </div>

      {/* Report card */}
      {report && (
        <div className="card" style={{ marginBottom: 20 }}>
          <SectionTitle>
            Rapport
            {report.approved
              ? <span style={{ color: 'var(--success)', fontSize: 13, fontWeight: 500, marginLeft: 8 }}>✓ Freigegeben</span>
              : <span style={{ color: 'var(--warning)', fontSize: 13, fontWeight: 500, marginLeft: 8 }}>⏳ Ausstehend</span>
            }
          </SectionTitle>
          <InfoRow label="Arbeitsbeschreibung" value={report.workDescription} />
          <InfoRow label="Arbeitsstunden" value={`${report.workingHours} h`} />
          {report.usedMaterials && <InfoRow label="Verwendetes Material" value={report.usedMaterials} />}
          <InfoRow label="Erstellt am" value={fmt(report.createdAt)} />
        </div>
      )}

      {/* Workflow actions */}
      <div className="card">
        <SectionTitle>Aktionen</SectionTitle>
        <div style={{ display: 'flex', gap: 10, flexWrap: 'wrap' }}>

          {/* Schritt 2: Disponieren */}
          {order.status === 'CREATED' && isManager && (
            <button className="btn-primary" onClick={() => setShowDispatch(true)} disabled={actionLoading}>
              📋 Disponieren
            </button>
          )}

          {/* Schritt 3a: Starten */}
          {order.status === 'SCHEDULED' && (
            <button className="btn-primary" onClick={() => doAction(() => workOrderApi.start(orderId))} disabled={actionLoading}>
              ▶️ Starten
            </button>
          )}

          {/* Schritt 3b: Rapport erfassen (auto-completes order) */}
          {order.status === 'IN_PROGRESS' && !report && (
            <button className="btn-success" onClick={() => setShowReport(true)} disabled={actionLoading}>
              📝 Rapport erfassen
            </button>
          )}

          {/* Schritt 5: Rapport freigeben (BL) */}
          {order.status === 'COMPLETED' && isManager && (
            <button className="btn-success" onClick={() => doAction(() => workOrderApi.approveReport(orderId))} disabled={actionLoading}>
              ✅ Rapport freigeben
            </button>
          )}

          {/* Schritt 6: Verrechnen (Admin) */}
          {order.status === 'APPROVED' && isAdmin && (
            <button className="btn-primary" onClick={() => doAction(() => workOrderApi.invoice(orderId))} disabled={actionLoading}>
              💶 Verrechnen
            </button>
          )}

          {/* Print */}
          <button className="btn-secondary" onClick={() => window.print()}>
            🖨️ Drucken
          </button>
        </div>

        {/* Status hint */}
        <div style={{ marginTop: 12, fontSize: 12, color: 'var(--gray-500)' }}>
          Ablauf: Erfasst → Disponiert → In Bearbeitung → Abgeschlossen → Freigegeben → Verrechnet
        </div>
      </div>

      {/* ── Dispatch Modal ── */}
      {showDispatch && (
        <div className="modal-overlay" onClick={() => setShowDispatch(false)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Auftrag disponieren</h2>
              <button className="close-btn" onClick={() => setShowDispatch(false)}>×</button>
            </div>
            <div className="modal-body">
              <div className="form-group">
                <label>Mitarbeiter *</label>
                <select value={selectedEmployee} onChange={e => setSelectedEmployee(e.target.value)}>
                  <option value="">– Mitarbeiter wählen –</option>
                  {employees.map(e => (
                    <option key={e.id} value={e.id}>
                      {e.fullName} ({e.role})
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>Termin (optional)</label>
                <input
                  type="datetime-local"
                  value={scheduledAt}
                  onChange={e => setScheduledAt(e.target.value)}
                />
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn-secondary" onClick={() => setShowDispatch(false)}>Abbrechen</button>
              <button
                className="btn-primary"
                onClick={handleDispatch}
                disabled={!selectedEmployee || actionLoading}
              >
                {actionLoading ? 'Speichern...' : 'Disponieren'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* ── Report Modal ── */}
      {showReport && (
        <div className="modal-overlay" onClick={() => setShowReport(false)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Rapport erfassen</h2>
              <button className="close-btn" onClick={() => setShowReport(false)}>×</button>
            </div>
            <div className="modal-body">
              <div className="form-group">
                <label>Arbeitsbeschreibung *</label>
                <textarea
                  value={reportForm.workDescription}
                  onChange={e => setReportForm(f => ({ ...f, workDescription: e.target.value }))}
                  placeholder="Was wurde ausgeführt?"
                  autoFocus
                />
              </div>
              <div className="form-group">
                <label>Arbeitsstunden *</label>
                <input
                  type="number"
                  step="0.25"
                  min="0.25"
                  max="24"
                  value={reportForm.workingHours}
                  onChange={e => setReportForm(f => ({ ...f, workingHours: e.target.value }))}
                  placeholder="z.B. 2.5"
                />
              </div>
              <div className="form-group">
                <label>Verwendetes Material</label>
                <textarea
                  value={reportForm.usedMaterials}
                  onChange={e => setReportForm(f => ({ ...f, usedMaterials: e.target.value }))}
                  placeholder="z.B. 2× Dichtung DN50, 1× Kugelhahn ½"
                  style={{ minHeight: 60 }}
                />
              </div>
              {reportError && <p className="error-msg">{reportError}</p>}
              <p style={{ fontSize: 12, color: 'var(--gray-500)', marginTop: 8 }}>
                ℹ️ Der Auftrag wird nach dem Speichern automatisch als «Abgeschlossen» markiert.
              </p>
            </div>
            <div className="modal-footer">
              <button className="btn-secondary" onClick={() => setShowReport(false)}>Abbrechen</button>
              <button className="btn-success" onClick={handleCreateReport} disabled={actionLoading}>
                {actionLoading ? 'Speichern...' : 'Rapport speichern'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

function SectionTitle({ children }: { children: React.ReactNode }) {
  return (
    <h3 style={{
      fontSize: 11, fontWeight: 700, color: 'var(--gray-500)',
      textTransform: 'uppercase', letterSpacing: '0.08em', marginBottom: 12,
    }}>
      {children}
    </h3>
  )
}

function InfoRow({ label, value }: { label: string; value: string }) {
  return (
    <div style={{ display: 'flex', gap: 8, marginBottom: 8, fontSize: 14, alignItems: 'flex-start' }}>
      <span style={{ color: 'var(--gray-500)', minWidth: 140, flexShrink: 0 }}>{label}</span>
      <span style={{ fontWeight: 500, wordBreak: 'break-word' }}>{value}</span>
    </div>
  )
}
