"use client";

import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { pixApi } from "@/lib/api/pix";
import { DASHBOARD_KEY } from "@/features/dashboard/hooks/use-dashboard";
import { STATEMENT_KEY, BALANCE_KEY } from "@/features/account/hooks/use-account";
import type { PixTransferFormData } from "../schemas";
import type { PixTransferResponse } from "@/lib/api/pix";

export function usePixTransfer(onSuccess?: (data: PixTransferResponse) => void) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: PixTransferFormData) => pixApi.transfer(data),
    onSuccess: (data) => {
      const status = data.status;
      if (status === "COMPLETED") {
        toast.success(`Pix enviado com sucesso!`);
      } else if (status === "PENDING") {
        toast.info("Pix em processamento.");
      } else {
        toast.error("Pix falhou. Tente novamente.");
      }
      queryClient.invalidateQueries({ queryKey: DASHBOARD_KEY });
      queryClient.invalidateQueries({ queryKey: STATEMENT_KEY });
      queryClient.invalidateQueries({ queryKey: BALANCE_KEY });
      onSuccess?.(data);
    },
    onError: (error: { response?: { data?: { message?: string } } }) => {
      toast.error(error.response?.data?.message ?? "Erro ao processar Pix.");
    },
  });
}
