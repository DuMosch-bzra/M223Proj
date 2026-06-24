import api from './client'
import type { WorkOrder, Customer, Employee, Report, AuthUser, OrderStatus } from '../types'

// ── Auth ──────────────────────────────────────────────────────────────────────
export const authApi = {
  login: (email: string, password: string) =>
    api.post<AuthUser>('/auth/login', { email, password }).then(r => r.data),

  register: (data: {
    firstName: string
    lastName: string
    email: string
    password: string
    role: string
  }) => api.post<AuthUser>('/auth/register', data).then(r => r.data),
}

// ── Work Orders ───────────────────────────────────────────────────────────────
export const workOrderApi = {
  getAll: (status?: OrderStatus) =>
    api.get<WorkOrder[]>('/work-orders', { params: status ? { status } : {} }).then(r => r.data),

  getById: (id: number) =>
    api.get<WorkOrder>(`/work-orders/${id}`).then(r => r.data),

  create: (data: {
    title: string
    description: string
    address: string
    customerId: number
  }) => api.post<WorkOrder>('/work-orders', data).then(r => r.data),

  update: (id: number, data: Partial<{ title: string; description: string; address: string }>) =>
    api.put<WorkOrder>(`/work-orders/${id}`, data).then(r => r.data),

  dispatch: (id: number, employeeId: number, scheduledAt?: string) =>
    api.patch<WorkOrder>(`/work-orders/${id}/dispatch`, { employeeId, scheduledAt }).then(r => r.data),

  start: (id: number) =>
    api.patch<WorkOrder>(`/work-orders/${id}/start`).then(r => r.data),

  complete: (id: number) =>
    api.patch<WorkOrder>(`/work-orders/${id}/complete`).then(r => r.data),

  approveReport: (id: number) =>
    api.patch<WorkOrder>(`/work-orders/${id}/approve-report`).then(r => r.data),

  invoice: (id: number) =>
    api.patch<WorkOrder>(`/work-orders/${id}/invoice`).then(r => r.data),

  delete: (id: number) =>
    api.delete(`/work-orders/${id}`),
}

// ── Customers ─────────────────────────────────────────────────────────────────
export const customerApi = {
  getAll: () =>
    api.get<Customer[]>('/customers').then(r => r.data),

  getById: (id: number) =>
    api.get<Customer>(`/customers/${id}`).then(r => r.data),

  create: (data: Omit<Customer, 'id'>) =>
    api.post<Customer>('/customers', data).then(r => r.data),

  update: (id: number, data: Omit<Customer, 'id'>) =>
    api.put<Customer>(`/customers/${id}`, data).then(r => r.data),
}

// ── Employees ─────────────────────────────────────────────────────────────────
export const employeeApi = {
  getAll: () =>
    api.get<Employee[]>('/employees').then(r => r.data),
}

// ── Reports ───────────────────────────────────────────────────────────────────
export const reportApi = {
  get: (workOrderId: number) =>
    api.get<Report>(`/work-orders/${workOrderId}/report`).then(r => r.data),

  create: (
    workOrderId: number,
    data: { workDescription: string; workingHours: number; usedMaterials?: string }
  ) => api.post<Report>(`/work-orders/${workOrderId}/report`, data).then(r => r.data),

  approve: (workOrderId: number) =>
    api.patch<Report>(`/work-orders/${workOrderId}/report/approve`).then(r => r.data),
}
