"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Receipt, CheckCircle2, XCircle, Clock } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { cn, formatCurrency, formatDate } from "@/lib/utils";
import { boletoPaySchema, type BoletoPayFormData } from "@/features/boleto/schemas";
import { useBoletoPayment } from "@/features/boleto/hooks/use-boleto";
import type { BoletoPaymentResponse } from "@/lib/api/boleto";

const statusConfig = {
  PAID: { label: "Pago", icon: CheckCircle2, color: "text-emerald-500" },
  PENDING: { label: "Pendente", icon: Clock, color: "text-amber-500" },
  FAILED: { label: "Falhou", icon: XCircle, color: "text-destructive" },
};

function BoletoResult({ result, onNew }: { result: BoletoPaymentResponse; onNew: () => void }) {
  const status = statusConfig[result.status];
  const Icon = status.icon;

  return (
    <Card>
      <CardContent className="p-6 space-y-4 text-center">
        <div className={cn("flex justify-center", status.color)}>
          <Icon className="size-14" />
        </div>
        <div>
          <h2 className="text-xl font-bold">{status.label}</h2>
          <p className="text-3xl font-bold mt-2">{formatCurrency(result.amount)}</p>
        </div>
        <Separator />
        <div className="space-y-2 text-sm text-left">
          <div className="flex justify-between gap-4">
            <span className="text-muted-foreground shrink-0">Código</span>
            <span className="font-mono text-xs truncate">{result.barcode}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-muted-foreground">Data</span>
            <span className="font-medium">{formatDate(result.paymentDate, "dd/MM/yyyy HH:mm")}</span>
          </div>
          {result.description && (
            <div className="flex justify-between">
              <span className="text-muted-foreground">Descrição</span>
              <span className="font-medium">{result.description}</span>
            </div>
          )}
        </div>
        <Button onClick={onNew} className="w-full">Novo boleto</Button>
      </CardContent>
    </Card>
  );
}

export default function BoletoPage() {
  const [result, setResult] = useState<BoletoPaymentResponse | null>(null);
  const { mutate: pay, isPending } = useBoletoPayment((data) => setResult(data));

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<BoletoPayFormData>({
    resolver: zodResolver(boletoPaySchema),
  });

  if (result) {
    return (
      <div className="flex flex-col gap-4 p-4 max-w-md mx-auto w-full">
        <h1 className="text-xl font-bold">Boleto</h1>
        <BoletoResult result={result} onNew={() => { setResult(null); reset(); }} />
      </div>
    );
  }

  return (
    <div className="flex flex-col gap-6 p-4 max-w-md mx-auto w-full">
      <div className="flex items-center gap-3">
        <div className="flex items-center justify-center size-10 rounded-xl bg-amber-400/10">
          <Receipt className="size-5 text-amber-400" />
        </div>
        <h1 className="text-xl font-bold">Pagar Boleto</h1>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Dados do boleto</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit((data) => pay(data))} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="barcode">Código de barras</Label>
              <Input
                id="barcode"
                placeholder="Digite o código de barras (44–60 dígitos)"
                inputMode="numeric"
                className={errors.barcode ? "border-destructive" : ""}
                {...register("barcode")}
              />
              {errors.barcode && (
                <p className="text-xs text-destructive">{errors.barcode.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="amount">Valor (R$)</Label>
              <Input
                id="amount"
                type="number"
                step="0.01"
                min="0.01"
                placeholder="0,00"
                className={errors.amount ? "border-destructive" : ""}
                {...register("amount", { valueAsNumber: true })}
              />
              {errors.amount && (
                <p className="text-xs text-destructive">{errors.amount.message}</p>
              )}
            </div>

            <Button type="submit" className="w-full gap-2" disabled={isPending}>
              <Receipt className="size-4" />
              {isPending ? "Processando..." : "Pagar boleto"}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
