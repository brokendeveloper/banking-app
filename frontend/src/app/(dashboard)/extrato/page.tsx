"use client";

import { useStatement, useBalance } from "@/features/account/hooks/use-account";
import { StatementAccordion } from "@/features/account/components/statement-accordion";
import { DepositDrawer } from "@/features/account/components/deposit-drawer";
import { Skeleton } from "@/components/ui/skeleton";
import { formatCurrency } from "@/lib/utils";

export default function ExtratoPage() {
  const { data: transactions, isLoading } = useStatement();
  const { data: balance, isLoading: balanceLoading } = useBalance();

  return (
    <div className="flex flex-col gap-6 p-6 max-w-2xl mx-auto w-full">
      <div className="flex items-center justify-between">
        <div className="space-y-0.5">
          <h1 className="text-lg font-semibold tracking-tight">Extrato</h1>
          <p className="text-xs text-muted-foreground/60">
            Saldo:{" "}
            {balanceLoading ? (
              <Skeleton className="inline-block h-3 w-16 align-middle" />
            ) : (
              <span className="font-medium text-foreground/80">{formatCurrency(balance ?? 0)}</span>
            )}
          </p>
        </div>
        <DepositDrawer />
      </div>

      <StatementAccordion transactions={transactions} isLoading={isLoading} />
    </div>
  );
}
