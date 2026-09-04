"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { cardsApi } from "@/lib/api/cards";
import type { CreateCardFormData } from "../schemas";

export const CARDS_KEY = ["cards"] as const;

export function useCards() {
  return useQuery({
    queryKey: CARDS_KEY,
    queryFn: cardsApi.getAll,
  });
}

export function useCreateCard(onSuccess?: () => void) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: CreateCardFormData) => cardsApi.create(data),
    onSuccess: () => {
      toast.success("Cartão criado com sucesso!");
      queryClient.invalidateQueries({ queryKey: CARDS_KEY });
      onSuccess?.();
    },
    onError: (error: { response?: { data?: { message?: string } } }) => {
      toast.error(error.response?.data?.message ?? "Erro ao criar cartão.");
    },
  });
}

export function useBlockCard() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, blocked }: { id: string; blocked: boolean }) =>
      blocked ? cardsApi.unblock(id) : cardsApi.block(id),
    onSuccess: (data) => {
      toast.success(data.message ?? (data.blocked ? "Cartão bloqueado." : "Cartão desbloqueado."));
      queryClient.invalidateQueries({ queryKey: CARDS_KEY });
    },
    onError: () => {
      toast.error("Erro ao alterar status do cartão.");
    },
  });
}
