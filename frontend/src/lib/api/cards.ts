import { api } from './client';

export interface CardResponse {
  id: string;
  cardNumber: string;
  holderName: string;
  expiration: string;
  blocked: boolean;
  createdAt: string;
}

export interface CreateCardRequest {
  holderName: string;
}

export interface BlockUnblockResponse {
  id: string;
  blocked: boolean;
  message: string;
}

export const cardsApi = {
  getAll: () =>
    api.get<CardResponse[]>('/cards').then((r) => r.data),

  create: (data: CreateCardRequest) =>
    api.post<CardResponse>('/cards', data).then((r) => r.data),

  block: (id: string) =>
    api.patch<BlockUnblockResponse>(`/cards/${id}/block`).then((r) => r.data),

  unblock: (id: string) =>
    api.patch<BlockUnblockResponse>(`/cards/${id}/unblock`).then((r) => r.data),
};
