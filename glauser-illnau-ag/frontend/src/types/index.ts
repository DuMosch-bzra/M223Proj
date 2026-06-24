export type OrderStatus =
  | 'CREATED'
  | 'SCHEDULED'
  | 'IN_PROGRESS'
  | 'COMPLETED'
  | 'APPROVED'
  | 'INVOICED'

export type Role = 'ADMIN' | 'MANAGER' | 'EMPLOYEE'

export interface WorkOrder {
  id: number
  title: string
  description: string
  address: string
  status: OrderStatus
  createdAt: string
  scheduledAt?: string
  completedAt?: string
  invoicedAt?: string
  customerId: number
  customerName: string
  employeeId?: number
  employeeName?: string
}

export interface Customer {
  id: number
  companyName: string
  contactPerson?: string
  phoneNumber?: string
  email?: string
  address?: string
}

export interface Employee {
  id: number
  fullName: string
  email: string
  role: Role
}

export interface Report {
  id: number
  workDescription: string
  workingHours: number
  usedMaterials?: string
  approved: boolean
  createdAt: string
  workOrderId: number
}

export interface AuthUser {
  token: string
  email: string
  fullName: string
  role: Role
}

export const STATUS_LABELS: Record<OrderStatus, string> = {
  CREATED: 'Erfasst',
  SCHEDULED: 'Disponiert',
  IN_PROGRESS: 'In Bearbeitung',
  COMPLETED: 'Abgeschlossen',
  APPROVED: 'Freigegeben',
  INVOICED: 'Verrechnet',
}

export const STATUS_COLORS: Record<OrderStatus, string> = {
  CREATED: '#6b7280',
  SCHEDULED: '#3b82f6',
  IN_PROGRESS: '#f59e0b',
  COMPLETED: '#8b5cf6',
  APPROVED: '#10b981',
  INVOICED: '#059669',
}
