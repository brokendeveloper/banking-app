"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { CheckCircle2, XCircle, Clock } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Card, CardContent } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { cn, formatCurrency, formatDate } from "@/lib/utils";
import { pixTransferSchema, type PixTransferFormData } from "@/features/pix/schemas";
import { usePixTransfer } from "@/features/pix/hooks/use-pix";
import type { PixTransferResponse } from "@/lib/api/pix";

const keyTypeLabels = {
  EMAIL: "E-mail",
  CPF: "CPF",
  PHONE: "Telefone",
  RANDOM: "Chave aleatória",
};

const statusConfig = {
  COMPLETED: { label: "Concluído", icon: CheckCircle2, color: "text-primary" },
  PENDING: { label: "Pendente", icon: Clock, color: "text-muted-foreground" },
  FAILED: { label: "Falhou", icon: XCircle, color: "text-destructive" },
  CANCELED: { label: "Cancelado", icon: XCircle, color: "text-muted-foreground/60" },
};

function PixResult({ result, onNew }: { result: PixTransferResponse; onNew: () => void }) {
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
          <div className="flex justify-between items-center">
            <span className="text-muted-foreground/70">Para</span>
            <span className="font-medium text-sm">{result.pixKey}</span>
          </div>
          <div className="flex justify-between items-center">
            <span className="text-muted-foreground/70">Tipo</span>
            <span className="font-medium">{keyTypeLabels[result.pixKeyType]}</span>
          </div>
          <div className="flex justify-between items-center">
            <span className="text-muted-foreground/70">Data</span>
            <span className="font-medium">{formatDate(result.timestamp, "dd/MM/yyyy HH:mm")}</span>
          </div>
          {result.description && (
            <div className="flex justify-between items-center">
              <span className="text-muted-foreground/70">Descrição</span>
              <span className="font-medium truncate max-w-[180px]">{result.description}</span>
            </div>
          )}
        </div>
        <Button
          onClick={onNew}
          variant="outline"
          className="w-full border-border/60 text-sm font-medium hover:bg-muted/60 transition-colors duration-150"
        >
          Nova transferência
        </Button>
      </CardContent>
    </Card>
  );
}

export default function PixPage() {
  const [result, setResult] = useState<PixTransferResponse | null>(null);
  const { mutate: transfer, isPending } = usePixTransfer((data) => setResult(data));

  const {
    register,
    handleSubmit,
    setValue,
    watch,
    reset,
    formState: { errors },
  } = useForm<PixTransferFormData>({
    resolver: zodResolver(pixTransferSchema),
  });

  const pixKeyType = watch("pixKeyType");

  const placeholders = {
    EMAIL: "email@exemplo.com",
    CPF: "00000000000",
    PHONE: "11999999999",
    RANDOM: "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
    undefined: "Selecione o tipo primeiro",
  };

  if (result) {
    return (
      <div className="flex flex-col gap-6 p-6 max-w-md mx-auto w-full">
        <h1 className="text-lg font-semibold tracking-tight">Pix</h1>
        <PixResult result={result} onNew={() => { setResult(null); reset(); }} />
      </div>
    );
  }

  return (
    <div className="flex flex-col gap-6 p-6 max-w-md mx-auto w-full">
      <div>
        <h1 className="text-lg font-semibold tracking-tight">Pix</h1>
        <p className="text-xs text-muted-foreground/60 mt-0.5">Transferência instantânea</p>
      </div>

      <Card className="border-border/50">
        <CardContent className="p-6">
          <form onSubmit={handleSubmit((data) => transfer(data))} className="space-y-5">
            <div className="space-y-1.5">
              <Label className="text-xs font-medium text-muted-foreground uppercase tracking-wider">
                Tipo de chave
              </Label>
              <Select onValueChange={(v) => setValue("pixKeyType", v as PixTransferFormData["pixKeyType"])}>
                <SelectTrigger className={cn(
                  "h-10 border-border/60 bg-transparent text-sm focus:border-primary/60 transition-colors duration-150",
                  errors.pixKeyType && "border-destructive/60"
                )}>
                  <SelectValue placeholder="Selecione o tipo de chave" />
                </SelectTrigger>
                <SelectContent>
                  {Object.entries(keyTypeLabels).map(([value, label]) => (
                    <SelectItem key={value} value={value}>{label}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {errors.pixKeyType && (
                <p className="text-xs text-destructive/80">{errors.pixKeyType.message}</p>
              )}
            </div>

            <div className="space-y-1.5">
              <Label htmlFor="pixKey" className="text-xs font-medium text-muted-foreground uppercase tracking-wider">
                Chave Pix
              </Label>
              <Input
                id="pixKey"
                placeholder={placeholders[pixKeyType ?? "undefined"]}
                className={cn(
                  "h-10 border-border/60 bg-transparent text-sm focus-visible:border-primary/60 focus-visible:ring-primary/20 transition-colors duration-150",
                  errors.pixKey && "border-destructive/60"
                )}
                {...register("pixKey")}
              />
              {errors.pixKey && (
                <p className="text-xs text-destructive/80">{errors.pixKey.message}</p>
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
                {isPending ? "Enviando..." : "Enviar Pix"}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
