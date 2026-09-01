"use client";

import { useDashboard } from "@/features/dashboard/hooks/use-dashboard";
import { BalanceCard } from "@/features/dashboard/components/balance-card";
import { RecentTransactions } from "@/features/dashboard/components/recent-transactions";
import { QuickActions } from "@/features/dashboard/components/quick-actions";
import Link from "next/link";
import { Bell } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useNotificationCount } from "@/features/notifications/hooks/use-notifications";
import { Badge } from "@/components/ui/badge";

export default function DashboardPage() {
  const { data, isLoading } = useDashboard();
  const { data: unreadCount } = useNotificationCount();

  return (
    <div className="flex flex-col gap-4 p-4 max-w-2xl mx-auto w-full">
      {/* Header mobile */}
      <div className="flex items-center justify-between md:hidden">
        <div>
          <p className="text-xs text-muted-foreground">Cesar Bank</p>
          {data?.name && (
            <p className="text-sm font-semibold">Olá, {data.name.split(" ")[0]}</p>
          )}
        </div>
        <Button variant="ghost" size="icon" render={<Link href="/notificacoes" className="relative" />}>
          <Bell className="size-5" />
          {unreadCount && unreadCount > 0 ? (
            <Badge
              variant="destructive"
              className="absolute -top-1 -right-1 size-4 p-0 text-[10px] flex items-center justify-center"
            >
              {unreadCount > 9 ? "9+" : unreadCount}
            </Badge>
          ) : null}
        </Button>
      </div>

      <BalanceCard
        balance={data?.balance}
        name={data?.name}
        isLoading={isLoading}
      />

      <QuickActions />

      <RecentTransactions
        transactions={data?.lastTransactions}
        isLoading={isLoading}
      />
    </div>
  );
}
