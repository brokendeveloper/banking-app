import { api } from './client';

export type PixKeyType = 'EMAIL' | 'CPF' | 'PHONE' | 'RANDOM';
export type PixTransactionStatus = 'PENDING' | 'COMPLETED' | 'FAILED' | 'CANCELED';

export interface PixTransferRequest {
  pixKey: string;
  pixKeyType: PixKeyType;
  amount: number;
}

export interface PixTransferResponse {
  senderEmail: string;
  receiverEmail: string;
  amount: number;
  timestamp: string;
  status: PixTransactionStatus;
  description: string;
  pixKeyType: PixKeyType;
  pixKey: string;
}

export const pixApi = {
  transfer: (data: PixTransferRequest) =>
    api.post<PixTransferResponse>('/pix/transfer', data).then((r) => r.data),
};
