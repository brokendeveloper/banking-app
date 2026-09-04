"use client";

import { useMutation } from "@tanstack/react-query";
import { useRouter } from "next/navigation";
import { toast } from "sonner";
import { authApi } from "@/lib/api/auth";
import type { RegisterFormData } from "../schemas";

export function useRegister() {
  const router = useRouter();

  return useMutation({
    mutationFn: (data: RegisterFormData) => authApi.register(data),
    onSuccess: (data) => {
      toast.success(data.message ?? "Conta criada com sucesso! Faça login.");
      router.push("/login");
    },
    onError: (error: { response?: { data?: { message?: string } } }) => {
      const message =
        error.response?.data?.message ?? "Falha ao criar conta. Verifique os dados.";
      toast.error(message);
    },
  });
}
