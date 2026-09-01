import { api } from './client';

export interface NotificationResponse {
  id: string;
  title: string;
  message: string;
  createdAt: string;
  read: boolean;
}

export const notificationsApi = {
  getAll: () =>
    api.get<NotificationResponse[]>('/notifications').then((r) => r.data),

  markRead: (id: string) =>
    api.patch<NotificationResponse>(`/notifications/${id}/read`).then((r) => r.data),
};
