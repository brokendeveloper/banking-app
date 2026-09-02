"use client";

import { ArrowDownLeft, ArrowUpRight, FileText, TrendingUp } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { cn, formatCurrency, formatDate } from "@/lib/utils";
import type { TransactionStatement, TransactionType } from "@/lib/api/account";

const transactionConfig: Record<
  TransactionType,
  { label: string; icon: React.ElementType; isCredit: boolean }
> = {
  PIX_RECEIVED: { label: "Pix recebido", icon: ArrowDownLeft, isCredit: true },
  PIX_SENT: { label: "Pix enviado", icon: ArrowUpRight, isCredit: false },
  BOLETO_PAYMENT: { label: "Boleto pago", icon: FileText, isCredit: false },
  INVESTMENT: { label: "Investimento", icon: TrendingUp, isCredit: false },
};

interface RecentTransactionsProps {
  transactions?: TransactionStatement[];
  isLoading?: boolean;
}

export function RecentTransactions({ transactions, isLoading }: RecentTransactionsProps) {
  if (isLoading) {
    return (
      <Card className="border-border/50">
        <CardHeader className="pb-3">
          <CardTitle className="text-sm font-semibold text-foreground/80">Últimas transações</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          {Array.from({ length: 4 }).map((_, i) => (
            <div key={i} className="flex items-center gap-3">
              <Skeleton className="size-8 rounded-lg" />
              <div className="flex-1 space-y-1.5">
                <Skeleton className="h-3 w-28" />
                <Skeleton className="h-2.5 w-20" />
              </div>
              <Skeleton className="h-3 w-16" />
            </div>
          ))}
        </CardContent>
      </Card>
    );
  }

  if (!transactions?.length) {
    return (
      <Card className="border-border/50">
        <CardHeader className="pb-3">
          <CardTitle className="text-sm font-semibold text-foreground/80">Últimas transações</CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-sm text-muted-foreground text-center py-8">
            Nenhuma transação encontrada.
          </p>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card className="border-border/50">
      <CardHeader className="pb-3">
        <CardTitle className="text-sm font-semibold text-foreground/80">Últimas transações</CardTitle>
      </CardHeader>
      <CardContent className="px-6 pb-2">
        {transactions.map((tx, i) => {
          const config = transactionConfig[tx.type];
          const Icon = config.icon;
          return (
            <div
              key={i}
              className="flex items-center gap-3 py-3 border-b border-border/30 last:border-0"
            >
              <div className="flex items-center justify-center size-8 rounded-lg bg-muted/60 shrink-0">
                <Icon className="size-3.5 text-muted-foreground" />
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium truncate">{config.label}</p>
                {tx.description && (
                  <p className="text-xs text-muted-foreground/60 truncate mt-0.5">{tx.description}</p>
                )}
              </div>
              <div className="text-right shrink-0">
                <p className={cn(
                  "text-sm font-medium tabular-nums",
                  config.isCredit ? "text-primary" : "text-foreground"
                )}>
                  {config.isCredit ? "+" : "−"}{formatCurrency(tx.amount)}
                </p>
                <p className="text-[10px] text-muted-foreground/50 mt-0.5">{formatDate(tx.date)}</p>
              </div>
            </div>
          );
        })}
      </CardContent>
    </Card>
  );
}
