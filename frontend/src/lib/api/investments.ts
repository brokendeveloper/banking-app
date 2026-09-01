import { api } from './client';

export type InvestmentType = 'CDB' | 'TESOURO_DIRETO' | 'LCI' | 'LCA' | 'POUPANCA';

export interface InvestmentRequest {
  type: InvestmentType;
  amount: number;
}

export interface InvestmentResponse {
  id: string;
  type: InvestmentType;
  amount: number;
  investmentDate: string;
  expectedReturn: number;
  maturityDate: string;
  redeemed: boolean;
}

export const investmentsApi = {
  getAll: () =>
    api.get<InvestmentResponse[]>('/investments').then((r) => r.data),

  create: (data: InvestmentRequest) =>
    api.post<InvestmentResponse>('/investments', data).then((r) => r.data),

  redeem: (id: string) =>
    api.post<InvestmentResponse>(`/investments/${id}/redeem`).then((r) => r.data),
};
