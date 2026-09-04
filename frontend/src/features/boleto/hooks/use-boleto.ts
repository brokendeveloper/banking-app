"use client";

import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { boletoApi } from "@/lib/api/boleto";
import { DASHBOARD_KEY } from "@/features/dashboard/hooks/use-dashboard";
import { STATEMENT_KEY, BALANCE_KEY } from "@/features/account/hooks/use-account";
import type { BoletoPayFormData } from "../schemas";
import type { BoletoPaymentResponse } from "@/lib/api/boleto";

export function useBoletoPayment(onSuccess?: (data: BoletoPaymentResponse) => void) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: BoletoPayFormData) => boletoApi.pay(data),
    onSuccess: (data) => {
      if (data.status === "PAID") {
        toast.success("Boleto pago com sucesso!");
      } else if (data.status === "PENDING") {
        toast.info("Pagamento em processamento.");
      } else {
        toast.error("Pagamento falhou. Tente novamente.");
      }
      queryClient.invalidateQueries({ queryKey: DASHBOARD_KEY });
      queryClient.invalidateQueries({ queryKey: STATEMENT_KEY });
      queryClient.invalidateQueries({ queryKey: BALANCE_KEY });
      onSuccess?.(data);
    },
    onError: (error: { response?: { data?: { message?: string } } }) => {
      toast.error(error.response?.data?.message ?? "Erro ao pagar boleto.");
    },
  });
}
