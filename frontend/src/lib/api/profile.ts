import { api } from './client';
import type { CardResponse } from './cards';

export interface ProfileResponse {
  name: string;
  email: string;
  cpf: string;
  telephone: string;
  account: {
    id: string;
    balance: number;
  };
  cards: CardResponse[];
}

export interface UpdateProfileRequest {
  name?: string;
  email?: string;
  telephone?: string;
}

export interface UpdateProfileResponse {
  name: string;
  email: string;
  telephone: string;
}

export const profileApi = {
  get: () =>
    api.get<ProfileResponse>('/profile').then((r) => r.data),

  update: (data: UpdateProfileRequest) =>
    api.put<UpdateProfileResponse>('/profile', data).then((r) => r.data),

  verify: () =>
    api.get('/profile/verify').then((r) => r.data),
};
