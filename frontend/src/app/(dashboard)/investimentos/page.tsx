"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { TrendingUp, Plus, ArrowUpFromLine, CheckCircle2 } from "lucide-react";
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
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
  DialogFooter,
} from "@/components/ui/dialog";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog";
import { cn, formatCurrency, formatDate } from "@/lib/utils";
import { createInvestmentSchema, investmentTypes, type CreateInvestmentFormData } from "@/features/investments/schemas";
import { useInvestments, useCreateInvestment, useRedeemInvestment } from "@/features/investments/hooks/use-investments";
import type { InvestmentResponse } from "@/lib/api/investments";

const typeLabels: Record<string, string> = {
  CDB: "CDB",
  TESOURO_DIRETO: "Tesouro Direto",
  LCI: "LCI",
  LCA: "LCA",
  POUPANCA: "Poupança",
};

function InvestmentCard({ investment }: { investment: InvestmentResponse }) {
  const { mutate: redeem, isPending } = useRedeemInvestment();

  return (
    <Card className={cn(
      "border-border/50 transition-opacity duration-150",
      investment.redeemed && "opacity-50"
    )}>
      <CardContent className="p-5">
        <div className="flex items-start justify-between gap-3 mb-4">
          <div className="flex items-center gap-2">
            <Badge
              variant="outline"
              className="text-[10px] font-semibold border-border/60 text-muted-foreground rounded-md"
            >
              {typeLabels[investment.type]}
            </Badge>
            {investment.redeemed && (
              <span className="inline-flex items-center gap-1 text-[10px] text-muted-foreground/60">
                <CheckCircle2 className="size-3" />
                Resgatado
              </span>
            )}
          </div>
          <p className="text-base font-semibold tracking-tight">{formatCurrency(investment.amount)}</p>
        </div>
        <div className="space-y-2 text-xs text-muted-foreground/70 mb-4">
          <div className="flex justify-between">
            <span>Aplicado em</span>
            <span className="text-foreground/70">{formatDate(investment.investmentDate)}</span>
          </div>
          <div className="flex justify-between">
            <span>Vencimento</span>
            <span className="text-foreground/70">{formatDate(investment.maturityDate)}</span>
          </div>
          <div className="flex justify-between">
            <span>Retorno esperado</span>
            <span className="text-primary font-medium">{formatCurrency(investment.expectedReturn)}</span>
          </div>
        </div>
        {!investment.redeemed && (
          <AlertDialog>
            <AlertDialogTrigger
              render={
                <Button
                  variant="outline"
                  size="sm"
                  className="w-full gap-1.5 border-border/60 text-xs font-medium hover:bg-muted/60 transition-colors duration-150"
                />
              }
              disabled={isPending}
            >
              <ArrowUpFromLine className="size-3" />
              Resgatar
            </AlertDialogTrigger>
            <AlertDialogContent>
              <AlertDialogHeader>
                <AlertDialogTitle>Resgatar investimento?</AlertDialogTitle>
                <AlertDialogDescription>
                  Você receberá {formatCurrency(investment.amount)} de volta. Juros podem ser perdidos se resgatar antes do vencimento.
                </AlertDialogDescription>
              </AlertDialogHeader>
              <AlertDialogFooter>
                <AlertDialogCancel>Cancelar</AlertDialogCancel>
                <AlertDialogAction onClick={() => redeem(investment.id)}>
                  Confirmar resgate
                </AlertDialogAction>
              </AlertDialogFooter>
            </AlertDialogContent>
          </AlertDialog>
        )}
      </CardContent>
    </Card>
  );
}

function CreateInvestmentDialog() {
  const [open, setOpen] = useState(false);
  const { mutate: create, isPending } = useCreateInvestment(() => setOpen(false));

  const {
    register,
    handleSubmit,
    setValue,
    reset,
    formState: { errors },
  } = useForm<CreateInvestmentFormData>({
    resolver: zodResolver(createInvestmentSchema),
  });

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger render={
        <Button
          size="sm"
          className="gap-1.5 text-xs font-medium bg-primary hover:bg-primary/90 text-primary-foreground transition-colors duration-150"
        />
      }>
        <Plus className="size-3.5" />
        Investir
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Novo investimento</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit((data) => { create(data); reset(); })} className="space-y-4">
          <div className="space-y-1.5">
            <Label className="text-xs font-medium text-muted-foreground uppercase tracking-wider">
              Tipo de investimento
            </Label>
            <Select onValueChange={(v) => setValue("type", v as CreateInvestmentFormData["type"])}>
              <SelectTrigger className={cn(
                "h-10 border-border/60 text-sm",
                errors.type && "border-destructive/60"
              )}>
                <SelectValue placeholder="Selecione o tipo" />
              </SelectTrigger>
              <SelectContent>
                {investmentTypes.map((t) => (
                  <SelectItem key={t} value={t}>{typeLabels[t]}</SelectItem>
                ))}
              </SelectContent>
            </Select>
            {errors.type && <p className="text-xs text-destructive/80">{errors.type.message}</p>}
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="inv-amount" className="text-xs font-medium text-muted-foreground uppercase tracking-wider">
              Valor (R$)
            </Label>
            <Input
              id="inv-amount"
              type="number"
              step="0.01"
              min="0.01"
              placeholder="0,00"
              className={cn(
                "h-10 border-border/60 text-sm",
                errors.amount && "border-destructive/60"
              )}
              {...register("amount", { valueAsNumber: true })}
            />
            {errors.amount && <p className="text-xs text-destructive/80">{errors.amount.message}</p>}
          </div>
        </form>
        <DialogFooter>
          <Button
            variant="outline"
            className="border-border/60 text-sm"
            onClick={() => setOpen(false)}
          >
            Cancelar
          </Button>
          <Button
            className="text-sm bg-primary hover:bg-primary/90 text-primary-foreground transition-colors duration-150"
            onClick={handleSubmit((data) => { create(data); reset(); })}
            disabled={isPending}
          >
            {isPending ? "Investindo..." : "Confirmar"}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

export default function InvestimentosPage() {
  const { data: investments, isLoading } = useInvestments();
  const active = investments?.filter((i) => !i.redeemed) ?? [];
  const redeemed = investments?.filter((i) => i.redeemed) ?? [];

  return (
    <div className="flex flex-col gap-6 p-6 max-w-2xl mx-auto w-full">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-lg font-semibold tracking-tight">Investimentos</h1>
          <p className="text-xs text-muted-foreground/60 mt-0.5">Seu patrimônio aplicado</p>
        </div>
        <CreateInvestmentDialog />
      </div>

      {isLoading ? (
        <div className="grid gap-3 sm:grid-cols-2">
          {Array.from({ length: 3 }).map((_, i) => (
            <Skeleton key={i} className="h-36 rounded-lg" />
          ))}
        </div>
      ) : !investments?.length ? (
        <div className="text-center py-20 space-y-4">
          <div className="flex items-center justify-center size-14 rounded-xl bg-muted/60 mx-auto">
            <TrendingUp className="size-6 text-muted-foreground/50" />
          </div>
          <div className="space-y-1">
            <p className="text-sm font-medium text-foreground/70">Nenhum investimento ainda</p>
            <p className="text-xs text-muted-foreground/50">Comece a investir para ver seu dinheiro crescer.</p>
          </div>
        </div>
      ) : (
        <div className="space-y-8">
          {active.length > 0 && (
            <div>
              <p className="text-[10px] font-semibold text-muted-foreground/60 uppercase tracking-widest mb-3">
                Ativos ({active.length})
              </p>
              <div className="grid gap-3 sm:grid-cols-2">
                {active.map((inv) => (
                  <InvestmentCard key={inv.id} investment={inv} />
                ))}
              </div>
            </div>
          )}
          {redeemed.length > 0 && (
            <div>
              <p className="text-[10px] font-semibold text-muted-foreground/60 uppercase tracking-widest mb-3">
                Resgatados ({redeemed.length})
              </p>
              <div className="grid gap-3 sm:grid-cols-2">
                {redeemed.map((inv) => (
                  <InvestmentCard key={inv.id} investment={inv} />
                ))}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
