"use client";

import { useState } from "react";
import { Eye, EyeOff, TrendingUp } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { AnimatedNumber } from "@/components/shared/animated-number";
import { formatCurrency } from "@/lib/utils";

interface BalanceCardProps {
  balance?: number;
  name?: string;
  isLoading?: boolean;
}

export function BalanceCard({ balance = 0, name, isLoading }: BalanceCardProps) {
  const [visible, setVisible] = useState(true);

  if (isLoading) {
    return (
      <Card className="bg-primary text-primary-foreground border-0 shadow-xl">
        <CardContent className="p-6 space-y-4">
          <Skeleton className="h-4 w-32 bg-primary-foreground/20" />
          <Skeleton className="h-10 w-48 bg-primary-foreground/20" />
          <Skeleton className="h-4 w-24 bg-primary-foreground/20" />
        </CardContent>
      </Card>
    );
  }

  return (
    <Card className="bg-gradient-to-br from-primary to-primary/80 text-primary-foreground border-0 shadow-xl shadow-primary/25 overflow-hidden relative">
      <div className="absolute top-0 right-0 w-32 h-32 bg-white/5 rounded-full -translate-y-16 translate-x-16" />
      <div className="absolute bottom-0 left-0 w-24 h-24 bg-white/5 rounded-full translate-y-12 -translate-x-8" />
      <CardContent className="p-6 relative">
        <div className="flex items-center justify-between mb-4">
          <div>
            {name && (
              <p className="text-primary-foreground/70 text-sm font-medium">
                Olá, {name.split(" ")[0]}
              </p>
            )}
            <p className="text-primary-foreground/80 text-xs mt-0.5">Saldo disponível</p>
          </div>
          <Button
            variant="ghost"
            size="icon"
            className="text-primary-foreground/70 hover:text-primary-foreground hover:bg-primary-foreground/10"
            onClick={() => setVisible((v) => !v)}
          >
            {visible ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
          </Button>
        </div>

        <div className="mb-4">
          {visible ? (
            <p className="text-4xl font-bold tracking-tight">
              <AnimatedNumber
                value={balance}
                formatter={formatCurrency}
              />
            </p>
          ) : (
            <p className="text-4xl font-bold tracking-tight">R$ ••••••</p>
          )}
        </div>

        <div className="flex items-center gap-1.5 text-primary-foreground/70 text-xs">
          <TrendingUp className="size-3" />
          <span>Conta corrente ativa</span>
        </div>
      </CardContent>
    </Card>
  );
}
