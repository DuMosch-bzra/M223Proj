import { useEffect, useState } from 'react'
import { customerApi } from '../api/services'
import type { Customer } from '../types'

export default function CustomersPage() {
  const [customers, setCustomers] = useState<Customer[]>([])
  const [loading, setLoading] = useState(true)
  const [showCreate, setShowCreate] = useState(false)
  const [form, setForm] = useState<Omit<Customer, 'id'>>({
    companyName: '', contactPerson: '', phoneNumber: '', email: '', address: ''
  })
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  const load = () => {
    setLoading(true)
    customerApi.getAll().then(setCustomers).finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [])

  const handleCreate = async () => {
    if (!form.companyName.trim()) { setError('Firmenname ist pflicht.'); return }
    setSaving(true); setError('')
    try {
      await customerApi.create(form)
      setShowCreate(false)
      setForm({ companyName: '', contactPerson: '', phoneNumber: '', email: '', address: '' })
      load()
    } catch { setError('Fehler beim Erstellen.') }
    finally { setSaving(false) }
  }

  return (
    <div style={{ padding: 28 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h1 style={{ fontSize: 22, fontWeight: 700 }}>Kunden</h1>
        <button className="btn-primary" onClick={() => setShowCreate(true)}>+ Kunde erfassen</button>
      </div>

      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        {loading ? (
          <p style={{ padding: 24, color: 'var(--gray-500)' }}>Lädt...</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Firma</th>
                <th>Kontaktperson</th>
                <th>Telefon</th>
                <th>E-Mail</th>
                <th>Adresse</th>
              </tr>
            </thead>
            <tbody>
              {customers.map(c => (
                <tr key={c.id}>
                  <td style={{ fontWeight: 500 }}>{c.companyName}</td>
                  <td>{c.contactPerson ?? '–'}</td>
                  <td>{c.phoneNumber ?? '–'}</td>
                  <td>{c.email ?? '–'}</td>
                  <td style={{ color: 'var(--gray-500)' }}>{c.address ?? '–'}</td>
                </tr>
              ))}
              {customers.length === 0 && (
                <tr><td colSpan={5} style={{ textAlign: 'center', padding: 32, color: 'var(--gray-500)' }}>Keine Kunden erfasst</td></tr>
              )}
            </tbody>
          </table>
        )}
      </div>

      {showCreate && (
        <div className="modal-overlay" onClick={() => setShowCreate(false)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Kunde erfassen</h2>
              <button className="close-btn" onClick={() => setShowCreate(false)}>×</button>
            </div>
            <div className="modal-body">
              <div className="form-group">
                <label>Firmenname *</label>
                <input value={form.companyName} onChange={e => setForm(f => ({ ...f, companyName: e.target.value }))} autoFocus />
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label>Kontaktperson</label>
                  <input value={form.contactPerson} onChange={e => setForm(f => ({ ...f, contactPerson: e.target.value }))} />
                </div>
                <div className="form-group">
                  <label>Telefon</label>
                  <input value={form.phoneNumber} onChange={e => setForm(f => ({ ...f, phoneNumber: e.target.value }))} />
                </div>
              </div>
              <div className="form-group">
                <label>E-Mail</label>
                <input type="email" value={form.email} onChange={e => setForm(f => ({ ...f, email: e.target.value }))} />
              </div>
              <div className="form-group">
                <label>Adresse</label>
                <textarea value={form.address} onChange={e => setForm(f => ({ ...f, address: e.target.value }))} />
              </div>
              {error && <p className="error-msg">{error}</p>}
            </div>
            <div className="modal-footer">
              <button className="btn-secondary" onClick={() => setShowCreate(false)}>Abbrechen</button>
              <button className="btn-primary" onClick={handleCreate} disabled={saving}>
                {saving ? 'Speichern...' : 'Kunde erstellen'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
