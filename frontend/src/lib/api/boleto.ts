import { api } from './client';

export type BoletoPaymentStatus = 'PENDING' | 'PAID' | 'FAILED';

export interface BoletoPayRequest {
  barcode: string;
  amount: number;
}

export interface BoletoPaymentResponse {
  barcode: string;
  amount: number;
  paymentDate: string;
  status: BoletoPaymentStatus;
  description: string;
}

export const boletoApi = {
  pay: (data: BoletoPayRequest) =>
    api.post<BoletoPaymentResponse>('/boleto/pay', data).then((r) => r.data),
};
