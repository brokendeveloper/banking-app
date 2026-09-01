"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { investmentsApi } from "@/lib/api/investments";
import { DASHBOARD_KEY } from "@/features/dashboard/hooks/use-dashboard";
import { BALANCE_KEY } from "@/features/account/hooks/use-account";
import type { CreateInvestmentFormData } from "../schemas";

export const INVESTMENTS_KEY = ["investments"] as const;

export function useInvestments() {
  return useQuery({
    queryKey: INVESTMENTS_KEY,
    queryFn: investmentsApi.getAll,
  });
}

export function useCreateInvestment(onSuccess?: () => void) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: CreateInvestmentFormData) => investmentsApi.create(data),
    onSuccess: () => {
      toast.success("Investimento realizado com sucesso!");
      queryClient.invalidateQueries({ queryKey: INVESTMENTS_KEY });
      queryClient.invalidateQueries({ queryKey: DASHBOARD_KEY });
      queryClient.invalidateQueries({ queryKey: BALANCE_KEY });
      onSuccess?.();
    },
    onError: (error: { response?: { data?: { message?: string } } }) => {
      toast.error(error.response?.data?.message ?? "Erro ao realizar investimento.");
    },
  });
}

export function useRedeemInvestment() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => investmentsApi.redeem(id),
    onSuccess: () => {
      toast.success("Investimento resgatado com sucesso!");
      queryClient.invalidateQueries({ queryKey: INVESTMENTS_KEY });
      queryClient.invalidateQueries({ queryKey: DASHBOARD_KEY });
      queryClient.invalidateQueries({ queryKey: BALANCE_KEY });
    },
    onError: (error: { response?: { data?: { message?: string } } }) => {
      toast.error(error.response?.data?.message ?? "Erro ao resgatar investimento.");
    },
  });
}
