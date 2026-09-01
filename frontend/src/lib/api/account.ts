import { api } from './client';

export type TransactionType = 'PIX_SENT' | 'PIX_RECEIVED' | 'BOLETO_PAYMENT' | 'INVESTMENT';

export interface TransactionStatement {
  type: TransactionType;
  amount: number;
  date: string;
  description: string;
}

export interface BalanceResponse {
  balance: number;
}

export interface DepositRequest {
  amount: number;
}

export interface DepositResponse {
  balance: number;
  message: string;
}

export interface StatementResponse {
  transactions: TransactionStatement[];
}

export const accountApi = {
  getBalance: () =>
    api.get<BalanceResponse>('/account/balance').then((r) => r.data),

  deposit: (data: DepositRequest) =>
    api.post<DepositResponse>('/account/deposit', data).then((r) => r.data),

  getStatement: () =>
    api.get<StatementResponse>('/account/statement').then((r) => r.data),
};
