"use client";

import { useState } from "react";
import { Eye, EyeOff } from "lucide-react";
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
      <Card className="border-border/50">
        <CardContent className="p-6 space-y-4">
          <Skeleton className="h-3 w-24" />
          <Skeleton className="h-9 w-40" />
          <Skeleton className="h-3 w-20" />
        </CardContent>
      </Card>
    );
  }

  return (
    <Card className="border-border/50 bg-card">
      <CardContent className="p-6">
        <div className="flex items-start justify-between mb-5">
          <div className="space-y-0.5">
            {name && (
              <p className="text-xs text-muted-foreground font-medium">
                Olá, {name.split(" ")[0]}
              </p>
            )}
            <p className="text-xs text-muted-foreground/60 tracking-wide uppercase">
              Saldo disponível
            </p>
          </div>
          <Button
            variant="ghost"
            size="icon"
            className="size-7 text-muted-foreground/50 hover:text-muted-foreground hover:bg-transparent transition-colors duration-150 -mt-1 -mr-1"
            onClick={() => setVisible((v) => !v)}
          >
            {visible ? <EyeOff className="size-3.5" /> : <Eye className="size-3.5" />}
          </Button>
        </div>

        <div className="mb-5">
          {visible ? (
            <p className="text-4xl font-semibold tracking-tight text-foreground">
              <AnimatedNumber value={balance} formatter={formatCurrency} />
            </p>
          ) : (
            <p className="text-4xl font-semibold tracking-tight text-foreground">
              R$ ••••••
            </p>
          )}
        </div>

        <div className="flex items-center gap-2 pt-4 border-t border-border/40">
          <span className="inline-block size-1.5 rounded-full bg-primary" />
          <span className="text-xs text-muted-foreground">Conta corrente ativa</span>
        </div>
      </CardContent>
    </Card>
  );
}
