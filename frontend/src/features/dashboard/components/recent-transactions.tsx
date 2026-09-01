"use client";

import { ArrowDownLeft, ArrowUpRight, FileText, TrendingUp } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { Badge } from "@/components/ui/badge";
import { cn, formatCurrency, formatDate } from "@/lib/utils";
import type { TransactionStatement, TransactionType } from "@/lib/api/account";

const transactionConfig: Record<
  TransactionType,
  { label: string; icon: React.ElementType; colorClass: string; variant: "default" | "secondary" | "destructive" | "outline" }
> = {
  PIX_RECEIVED: { label: "Pix recebido", icon: ArrowDownLeft, colorClass: "text-emerald-500", variant: "secondary" },
  PIX_SENT: { label: "Pix enviado", icon: ArrowUpRight, colorClass: "text-destructive", variant: "destructive" },
  BOLETO_PAYMENT: { label: "Boleto pago", icon: FileText, colorClass: "text-destructive", variant: "destructive" },
  INVESTMENT: { label: "Investimento", icon: TrendingUp, colorClass: "text-primary", variant: "default" },
};

interface RecentTransactionsProps {
  transactions?: TransactionStatement[];
  isLoading?: boolean;
}

export function RecentTransactions({ transactions, isLoading }: RecentTransactionsProps) {
  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="text-base">Últimas transações</CardTitle>
        </CardHeader>
        <CardContent className="space-y-3">
          {Array.from({ length: 4 }).map((_, i) => (
            <div key={i} className="flex items-center gap-3">
              <Skeleton className="size-9 rounded-full" />
              <div className="flex-1 space-y-1.5">
                <Skeleton className="h-3.5 w-28" />
                <Skeleton className="h-3 w-20" />
              </div>
              <Skeleton className="h-4 w-20" />
            </div>
          ))}
        </CardContent>
      </Card>
    );
  }

  if (!transactions?.length) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="text-base">Últimas transações</CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-sm text-muted-foreground text-center py-6">
            Nenhuma transação encontrada.
          </p>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">Últimas transações</CardTitle>
      </CardHeader>
      <CardContent className="space-y-1">
        {transactions.map((tx, i) => {
          const config = transactionConfig[tx.type];
          const Icon = config.icon;
          const isCredit = tx.type === "PIX_RECEIVED";
          return (
            <div
              key={i}
              className="flex items-center gap-3 py-2.5 border-b border-border/40 last:border-0"
            >
              <div className={cn("flex items-center justify-center size-9 rounded-full bg-muted", config.colorClass)}>
                <Icon className="size-4" />
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium truncate">{config.label}</p>
                <p className="text-xs text-muted-foreground truncate">{tx.description}</p>
              </div>
              <div className="text-right">
                <p className={cn("text-sm font-semibold", isCredit ? "text-emerald-500" : "text-foreground")}>
                  {isCredit ? "+" : "-"}{formatCurrency(tx.amount)}
                </p>
                <p className="text-xs text-muted-foreground">{formatDate(tx.date)}</p>
              </div>
            </div>
          );
        })}
      </CardContent>
    </Card>
  );
}
