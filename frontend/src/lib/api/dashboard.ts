import { api } from './client';
import type { TransactionStatement } from './account';

export interface DashboardData {
  name: string;
  email: string;
  balance: number;
  lastTransactions: TransactionStatement[];
}

export const dashboardApi = {
  get: () => api.get<DashboardData>('/dashboard').then((r) => r.data),
};
