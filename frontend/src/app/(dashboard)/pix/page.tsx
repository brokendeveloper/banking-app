"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { ArrowLeftRight, CheckCircle2, XCircle, Clock } from "lucide-react";
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
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
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
  COMPLETED: { label: "Concluído", icon: CheckCircle2, color: "text-emerald-500" },
  PENDING: { label: "Pendente", icon: Clock, color: "text-amber-500" },
  FAILED: { label: "Falhou", icon: XCircle, color: "text-destructive" },
  CANCELED: { label: "Cancelado", icon: XCircle, color: "text-muted-foreground" },
};

function PixResult({ result, onNew }: { result: PixTransferResponse; onNew: () => void }) {
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
          <div className="flex justify-between">
            <span className="text-muted-foreground">Para</span>
            <span className="font-medium">{result.pixKey}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-muted-foreground">Tipo</span>
            <span className="font-medium">{keyTypeLabels[result.pixKeyType]}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-muted-foreground">Data</span>
            <span className="font-medium">{formatDate(result.timestamp, "dd/MM/yyyy HH:mm")}</span>
          </div>
          {result.description && (
            <div className="flex justify-between">
              <span className="text-muted-foreground">Descrição</span>
              <span className="font-medium truncate max-w-[180px]">{result.description}</span>
            </div>
          )}
        </div>
        <Button onClick={onNew} className="w-full">Novo Pix</Button>
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
      <div className="flex flex-col gap-4 p-4 max-w-md mx-auto w-full">
        <h1 className="text-xl font-bold">Pix</h1>
        <PixResult result={result} onNew={() => { setResult(null); reset(); }} />
      </div>
    );
  }

  return (
    <div className="flex flex-col gap-6 p-4 max-w-md mx-auto w-full">
      <div className="flex items-center gap-3">
        <div className="flex items-center justify-center size-10 rounded-xl bg-primary/10">
          <ArrowLeftRight className="size-5 text-primary" />
        </div>
        <h1 className="text-xl font-bold">Pix</h1>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Transferência via Pix</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit((data) => transfer(data))} className="space-y-4">
            <div className="space-y-2">
              <Label>Tipo de chave</Label>
              <Select onValueChange={(v) => setValue("pixKeyType", v as PixTransferFormData["pixKeyType"])}>
                <SelectTrigger className={errors.pixKeyType ? "border-destructive" : ""}>
                  <SelectValue placeholder="Selecione o tipo de chave" />
                </SelectTrigger>
                <SelectContent>
                  {Object.entries(keyTypeLabels).map(([value, label]) => (
                    <SelectItem key={value} value={value}>{label}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {errors.pixKeyType && (
                <p className="text-xs text-destructive">{errors.pixKeyType.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="pixKey">Chave Pix</Label>
              <Input
                id="pixKey"
                placeholder={placeholders[pixKeyType ?? "undefined"]}
                className={errors.pixKey ? "border-destructive" : ""}
                {...register("pixKey")}
              />
              {errors.pixKey && (
                <p className="text-xs text-destructive">{errors.pixKey.message}</p>
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
              <ArrowLeftRight className="size-4" />
              {isPending ? "Enviando Pix..." : "Enviar Pix"}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
