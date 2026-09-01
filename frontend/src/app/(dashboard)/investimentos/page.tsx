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
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
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

const typeColors: Record<string, string> = {
  CDB: "bg-primary/10 text-primary",
  TESOURO_DIRETO: "bg-emerald-500/10 text-emerald-500",
  LCI: "bg-amber-500/10 text-amber-500",
  LCA: "bg-sky-500/10 text-sky-500",
  POUPANCA: "bg-violet-500/10 text-violet-500",
};

function InvestmentCard({ investment }: { investment: InvestmentResponse }) {
  const { mutate: redeem, isPending } = useRedeemInvestment();

  return (
    <Card className={cn("transition-opacity", investment.redeemed && "opacity-60")}>
      <CardContent className="p-4">
        <div className="flex items-start justify-between gap-3 mb-3">
          <div className="flex items-center gap-2">
            <Badge className={cn("text-xs font-bold", typeColors[investment.type])}>
              {typeLabels[investment.type]}
            </Badge>
            {investment.redeemed && (
              <Badge variant="secondary" className="text-xs gap-1">
                <CheckCircle2 className="size-3" />
                Resgatado
              </Badge>
            )}
          </div>
          <p className="text-lg font-bold">{formatCurrency(investment.amount)}</p>
        </div>
        <div className="space-y-1 text-xs text-muted-foreground mb-3">
          <div className="flex justify-between">
            <span>Aplicado em</span>
            <span className="text-foreground">{formatDate(investment.investmentDate)}</span>
          </div>
          <div className="flex justify-between">
            <span>Vencimento</span>
            <span className="text-foreground">{formatDate(investment.maturityDate)}</span>
          </div>
          <div className="flex justify-between">
            <span>Retorno esperado</span>
            <span className="text-emerald-500 font-medium">{formatCurrency(investment.expectedReturn)}</span>
          </div>
        </div>
        {!investment.redeemed && (
          <AlertDialog>
            <AlertDialogTrigger
              render={<Button variant="outline" size="sm" className="w-full gap-2" />}
              disabled={isPending}
            >
              <ArrowUpFromLine className="size-3.5" />
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
      <DialogTrigger render={<Button className="gap-2" />}>
        <Plus className="size-4" />
        Investir
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Novo investimento</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit((data) => { create(data); reset(); })} className="space-y-4">
          <div className="space-y-2">
            <Label>Tipo de investimento</Label>
            <Select onValueChange={(v) => setValue("type", v as CreateInvestmentFormData["type"])}>
              <SelectTrigger className={errors.type ? "border-destructive" : ""}>
                <SelectValue placeholder="Selecione o tipo" />
              </SelectTrigger>
              <SelectContent>
                {investmentTypes.map((t) => (
                  <SelectItem key={t} value={t}>{typeLabels[t]}</SelectItem>
                ))}
              </SelectContent>
            </Select>
            {errors.type && <p className="text-xs text-destructive">{errors.type.message}</p>}
          </div>
          <div className="space-y-2">
            <Label htmlFor="inv-amount">Valor (R$)</Label>
            <Input
              id="inv-amount"
              type="number"
              step="0.01"
              min="0.01"
              placeholder="0,00"
              className={errors.amount ? "border-destructive" : ""}
              {...register("amount", { valueAsNumber: true })}
            />
            {errors.amount && <p className="text-xs text-destructive">{errors.amount.message}</p>}
          </div>
        </form>
        <DialogFooter>
          <Button variant="outline" onClick={() => setOpen(false)}>Cancelar</Button>
          <Button onClick={handleSubmit((data) => { create(data); reset(); })} disabled={isPending}>
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
    <div className="flex flex-col gap-6 p-4 max-w-2xl mx-auto w-full">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="flex items-center justify-center size-10 rounded-xl bg-emerald-400/10">
            <TrendingUp className="size-5 text-emerald-400" />
          </div>
          <h1 className="text-xl font-bold">Investimentos</h1>
        </div>
        <CreateInvestmentDialog />
      </div>

      {isLoading ? (
        <div className="grid gap-3 sm:grid-cols-2">
          {Array.from({ length: 3 }).map((_, i) => (
            <Skeleton key={i} className="h-36 rounded-xl" />
          ))}
        </div>
      ) : !investments?.length ? (
        <div className="text-center py-16 space-y-3">
          <div className="flex items-center justify-center size-16 rounded-2xl bg-muted mx-auto">
            <TrendingUp className="size-8 text-muted-foreground" />
          </div>
          <p className="text-sm text-muted-foreground">
            Você ainda não tem investimentos. <br />
            Comece a investir agora!
          </p>
        </div>
      ) : (
        <div className="space-y-6">
          {active.length > 0 && (
            <div>
              <p className="text-xs font-semibold text-muted-foreground uppercase tracking-wider mb-3">
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
              <p className="text-xs font-semibold text-muted-foreground uppercase tracking-wider mb-3">
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
