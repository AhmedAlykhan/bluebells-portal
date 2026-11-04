export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  isFirst: boolean;
  isLast: boolean;
}

export interface ValidationError {
  field: string;
  message: string;
  rejectedValue?: unknown;
}

export interface ApiError {
  type: string;
  title: string;
  status: number;
  detail: string;
  instance: string;
  timestamp: string;
  correlationId: string;
  validationErrors?: ValidationError[];
}

export interface SchoolProfile {
  id: number;
  name: string;
  address: string;
  email: string;
  phone: string;
  logoUrl?: string;
  currency: string;
  timezone: string;
}

export interface User {
  id: number;
  username: string;
  email: string;
  fullName: string;
  phoneNumber?: string;
  status: 'ACTIVE' | 'SUSPENDED' | 'PENDING_ACTIVATION';
  roles: string[];
}
