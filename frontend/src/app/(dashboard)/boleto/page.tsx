"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { CheckCircle2, XCircle, Clock } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { cn, formatCurrency, formatDate } from "@/lib/utils";
import { boletoPaySchema, type BoletoPayFormData } from "@/features/boleto/schemas";
import { useBoletoPayment } from "@/features/boleto/hooks/use-boleto";
import type { BoletoPaymentResponse } from "@/lib/api/boleto";

const statusConfig = {
  PAID: { label: "Pago", icon: CheckCircle2, color: "text-primary" },
  PENDING: { label: "Pendente", icon: Clock, color: "text-muted-foreground" },
  FAILED: { label: "Falhou", icon: XCircle, color: "text-destructive" },
};

function BoletoResult({ result, onNew }: { result: BoletoPaymentResponse; onNew: () => void }) {
  const status = statusConfig[result.status];
  const Icon = status.icon;

  return (
    <Card className="border-border/50">
      <CardContent className="p-8 space-y-6 text-center">
        <div className={cn("flex justify-center", status.color)}>
          <Icon className="size-10" strokeWidth={1.5} />
        </div>
        <div className="space-y-1">
          <p className="text-sm font-medium text-muted-foreground">{status.label}</p>
          <p className="text-3xl font-semibold tracking-tight">{formatCurrency(result.amount)}</p>
        </div>
        <Separator className="opacity-40" />
        <div className="space-y-3 text-sm text-left">
          <div className="flex justify-between items-start gap-4">
            <span className="text-muted-foreground/70 shrink-0">Código</span>
            <span className="font-mono text-xs truncate">{result.barcode}</span>
          </div>
          <div className="flex justify-between items-center">
            <span className="text-muted-foreground/70">Data</span>
            <span className="font-medium">{formatDate(result.paymentDate, "dd/MM/yyyy HH:mm")}</span>
          </div>
          {result.description && (
            <div className="flex justify-between items-center">
              <span className="text-muted-foreground/70">Descrição</span>
              <span className="font-medium">{result.description}</span>
            </div>
          )}
        </div>
        <Button
          onClick={onNew}
          variant="outline"
          className="w-full border-border/60 text-sm font-medium hover:bg-muted/60 transition-colors duration-150"
        >
          Novo boleto
        </Button>
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
      <div className="flex flex-col gap-6 p-6 max-w-md mx-auto w-full">
        <h1 className="text-lg font-semibold tracking-tight">Boleto</h1>
        <BoletoResult result={result} onNew={() => { setResult(null); reset(); }} />
      </div>
    );
  }

  return (
    <div className="flex flex-col gap-6 p-6 max-w-md mx-auto w-full">
      <div>
        <h1 className="text-lg font-semibold tracking-tight">Pagar Boleto</h1>
        <p className="text-xs text-muted-foreground/60 mt-0.5">Insira o código de barras</p>
      </div>

      <Card className="border-border/50">
        <CardContent className="p-6">
          <form onSubmit={handleSubmit((data) => pay(data))} className="space-y-5">
            <div className="space-y-1.5">
              <Label htmlFor="barcode" className="text-xs font-medium text-muted-foreground uppercase tracking-wider">
                Código de barras
              </Label>
              <Input
                id="barcode"
                placeholder="44 a 60 dígitos"
                inputMode="numeric"
                className={cn(
                  "h-10 border-border/60 bg-transparent text-sm font-mono focus-visible:border-primary/60 focus-visible:ring-primary/20 transition-colors duration-150",
                  errors.barcode && "border-destructive/60"
                )}
                {...register("barcode")}
              />
              {errors.barcode && (
                <p className="text-xs text-destructive/80">{errors.barcode.message}</p>
              )}
            </div>

            <div className="space-y-1.5">
              <Label htmlFor="amount" className="text-xs font-medium text-muted-foreground uppercase tracking-wider">
                Valor (R$)
              </Label>
              <Input
                id="amount"
                type="number"
                step="0.01"
                min="0.01"
                placeholder="0,00"
                className={cn(
                  "h-10 border-border/60 bg-transparent text-sm focus-visible:border-primary/60 focus-visible:ring-primary/20 transition-colors duration-150",
                  errors.amount && "border-destructive/60"
                )}
                {...register("amount", { valueAsNumber: true })}
              />
              {errors.amount && (
                <p className="text-xs text-destructive/80">{errors.amount.message}</p>
              )}
            </div>

            <div className="pt-1">
              <Button
                type="submit"
                className="w-full h-10 text-sm font-medium bg-primary hover:bg-primary/90 text-primary-foreground transition-colors duration-150"
                disabled={isPending}
              >
                {isPending ? "Processando..." : "Pagar boleto"}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
