import { api } from './client';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  name: string;
  email: string;
  token: string;
}

export interface RegisterRequest {
  name: string;
  cpf: string;
  email: string;
  password: string;
  telephone: string;
}

export interface RegisterResponse {
  name: string;
  email: string;
  message: string;
}

export const authApi = {
  login: (data: LoginRequest) =>
    api.post<LoginResponse>('/auth/login', data).then((r) => r.data),

  register: (data: RegisterRequest) =>
    api.post<RegisterResponse>('/auth/register', data).then((r) => r.data),

  verify: () =>
    api.get('/profile/verify').then((r) => r.data),
};
