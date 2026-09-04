import { api } from "./client";

export const newsletterApi = {
  subscribe: (email: string) =>
    api.post<{ message: string }>("/newsletter/subscribe", { email }),
};
