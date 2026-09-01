"use client";

import { useMemo } from "react";
import { ArrowDownLeft, ArrowUpRight, FileText, TrendingUp } from "lucide-react";
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import { cn, formatCurrency, formatDate, formatDateLong } from "@/lib/utils";
import type { TransactionStatement, TransactionType } from "@/lib/api/account";

const txConfig: Record<TransactionType, { label: string; icon: React.ElementType; colorClass: string; isCredit: boolean }> = {
  PIX_RECEIVED: { label: "Pix recebido", icon: ArrowDownLeft, colorClass: "text-emerald-500", isCredit: true },
  PIX_SENT: { label: "Pix enviado", icon: ArrowUpRight, colorClass: "text-destructive", isCredit: false },
  BOLETO_PAYMENT: { label: "Boleto pago", icon: FileText, colorClass: "text-destructive", isCredit: false },
  INVESTMENT: { label: "Investimento", icon: TrendingUp, colorClass: "text-primary", isCredit: false },
};

function groupByDate(transactions: TransactionStatement[]) {
  const groups = new Map<string, TransactionStatement[]>();
  for (const tx of transactions) {
    const dateKey = tx.date.slice(0, 10);
    if (!groups.has(dateKey)) groups.set(dateKey, []);
    groups.get(dateKey)!.push(tx);
  }
  return Array.from(groups.entries()).sort(([a], [b]) => b.localeCompare(a));
}

interface StatementAccordionProps {
  transactions?: TransactionStatement[];
  isLoading?: boolean;
}

export function StatementAccordion({ transactions, isLoading }: StatementAccordionProps) {
  const grouped = useMemo(
    () => groupByDate(transactions ?? []),
    [transactions]
  );

  if (isLoading) {
    return (
      <div className="space-y-3">
        {Array.from({ length: 3 }).map((_, i) => (
          <div key={i} className="space-y-2">
            <Skeleton className="h-4 w-32" />
            {Array.from({ length: 2 }).map((_, j) => (
              <Skeleton key={j} className="h-14 w-full" />
            ))}
          </div>
        ))}
      </div>
    );
  }

  if (!grouped.length) {
    return (
      <div className="text-center py-12 text-muted-foreground">
        <FileText className="size-10 mx-auto mb-3 opacity-40" />
        <p className="text-sm">Nenhuma transação encontrada.</p>
      </div>
    );
  }

  return (
    <Accordion multiple defaultValue={grouped.slice(0, 2).map(([d]) => d)} className="space-y-2">
      {grouped.map(([dateKey, txs]) => {
        const dayTotal = txs.reduce((sum, tx) => {
          const config = txConfig[tx.type];
          return sum + (config.isCredit ? tx.amount : -tx.amount);
        }, 0);

        return (
          <AccordionItem key={dateKey} value={dateKey} className="border border-border/50 rounded-xl px-4">
            <AccordionTrigger className="hover:no-underline">
              <div className="flex items-center justify-between w-full pr-2">
                <span className="text-sm font-semibold capitalize">
                  {formatDateLong(dateKey + "T00:00:00")}
                </span>
                <span className={cn("text-sm font-medium", dayTotal >= 0 ? "text-emerald-500" : "text-muted-foreground")}>
                  {dayTotal >= 0 ? "+" : ""}{formatCurrency(dayTotal)}
                </span>
              </div>
            </AccordionTrigger>
            <AccordionContent>
              <div className="space-y-0">
                {txs.map((tx, i) => {
                  const config = txConfig[tx.type];
                  const Icon = config.icon;
                  return (
                    <div
                      key={i}
                      className="flex items-center gap-3 py-3 border-b border-border/30 last:border-0"
                    >
                      <div className={cn("flex items-center justify-center size-9 rounded-full bg-muted shrink-0", config.colorClass)}>
                        <Icon className="size-4" />
                      </div>
                      <div className="flex-1 min-w-0">
                        <p className="text-sm font-medium">{config.label}</p>
                        <p className="text-xs text-muted-foreground truncate">{tx.description}</p>
                      </div>
                      <div className="text-right shrink-0">
                        <p className={cn("text-sm font-semibold", config.isCredit ? "text-emerald-500" : "text-foreground")}>
                          {config.isCredit ? "+" : "-"}{formatCurrency(tx.amount)}
                        </p>
                        <p className="text-xs text-muted-foreground">
                          {formatDate(tx.date, "HH:mm")}
                        </p>
                      </div>
                    </div>
                  );
                })}
              </div>
            </AccordionContent>
          </AccordionItem>
        );
      })}
    </Accordion>
  );
}
