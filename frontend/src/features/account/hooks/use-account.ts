"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { accountApi } from "@/lib/api/account";
import { DASHBOARD_KEY } from "@/features/dashboard/hooks/use-dashboard";

export const STATEMENT_KEY = ["statement"] as const;
export const BALANCE_KEY = ["balance"] as const;

export function useStatement() {
  return useQuery({
    queryKey: STATEMENT_KEY,
    queryFn: accountApi.getStatement,
    select: (data) => data.transactions,
  });
}

export function useBalance() {
  return useQuery({
    queryKey: BALANCE_KEY,
    queryFn: accountApi.getBalance,
    select: (data) => data.balance,
  });
}

export function useDeposit(onSuccess?: () => void) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: accountApi.deposit,
    onSuccess: (data) => {
      toast.success(data.message ?? "Depósito realizado com sucesso!");
      queryClient.invalidateQueries({ queryKey: STATEMENT_KEY });
      queryClient.invalidateQueries({ queryKey: BALANCE_KEY });
      queryClient.invalidateQueries({ queryKey: DASHBOARD_KEY });
      onSuccess?.();
    },
    onError: (error: { response?: { data?: { message?: string } } }) => {
      toast.error(error.response?.data?.message ?? "Erro ao realizar depósito.");
    },
  });
}
