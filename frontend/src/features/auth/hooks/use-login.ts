"use client";

import { useMutation } from "@tanstack/react-query";
import { useRouter } from "next/navigation";
import { toast } from "sonner";
import { authApi } from "@/lib/api/auth";
import { useAuthStore } from "@/lib/stores/auth-store";
import type { LoginFormData } from "../schemas";

export function useLogin() {
  const router = useRouter();
  const setAuth = useAuthStore((s) => s.setAuth);

  return useMutation({
    mutationFn: (data: LoginFormData) => authApi.login(data),
    onSuccess: (data) => {
      setAuth(data.token, { name: data.name, email: data.email });
      toast.success(`Bem-vindo, ${data.name}!`);
      router.push("/dashboard");
    },
    onError: (error: { response?: { data?: { message?: string } } }) => {
      const message =
        error.response?.data?.message ?? "Credenciais inválidas. Tente novamente.";
      toast.error(message);
    },
  });
}
