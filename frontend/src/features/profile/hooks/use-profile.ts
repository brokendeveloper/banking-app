"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { profileApi } from "@/lib/api/profile";
import { useAuthStore } from "@/lib/stores/auth-store";
import type { UpdateProfileFormData } from "../schemas";

export const PROFILE_KEY = ["profile"] as const;

export function useProfile() {
  return useQuery({
    queryKey: PROFILE_KEY,
    queryFn: profileApi.get,
  });
}

export function useUpdateProfile(onSuccess?: () => void) {
  const queryClient = useQueryClient();
  const setAuth = useAuthStore((s) => s.setAuth);
  const token = useAuthStore((s) => s.token);

  return useMutation({
    mutationFn: (data: UpdateProfileFormData) => {
      const clean = Object.fromEntries(
        Object.entries(data).filter(([, v]) => v !== "" && v !== undefined)
      );
      return profileApi.update(clean);
    },
    onSuccess: (data) => {
      toast.success("Perfil atualizado com sucesso!");
      // Update user name/email in auth store if changed
      if (token) {
        setAuth(token, { name: data.name, email: data.email });
      }
      queryClient.invalidateQueries({ queryKey: PROFILE_KEY });
      onSuccess?.();
    },
    onError: (error: { response?: { data?: { message?: string } } }) => {
      toast.error(error.response?.data?.message ?? "Erro ao atualizar perfil.");
    },
  });
}
