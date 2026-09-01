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
    <div className="flex flex-col gap-6 p-4 max-w-2xl mx-auto w-full">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold">Extrato</h1>
          <p className="text-sm text-muted-foreground">
            Saldo:{" "}
            {balanceLoading ? (
              <Skeleton className="inline-block h-4 w-20" />
            ) : (
              <span className="font-semibold text-foreground">{formatCurrency(balance ?? 0)}</span>
            )}
          </p>
        </div>
        <DepositDrawer />
      </div>

      <StatementAccordion transactions={transactions} isLoading={isLoading} />
    </div>
  );
}
