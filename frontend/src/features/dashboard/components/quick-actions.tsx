"use client";

import Link from "next/link";
import { ArrowLeftRight, CreditCard, Receipt, TrendingUp, ArrowDownToLine } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";

const actions = [
  { href: "/pix", label: "Pix", icon: ArrowLeftRight },
  { href: "/boleto", label: "Boleto", icon: Receipt },
  { href: "/investimentos", label: "Investir", icon: TrendingUp },
  { href: "/cartoes", label: "Cartões", icon: CreditCard },
  { href: "/extrato", label: "Depósito", icon: ArrowDownToLine },
];

export function QuickActions() {
  return (
    <Card className="border-border/50">
      <CardContent className="p-5">
        <p className="text-[10px] font-semibold text-muted-foreground/60 uppercase tracking-widest mb-4">
          Ações rápidas
        </p>
        <div className="grid grid-cols-5 gap-1">
          {actions.map((action) => {
            const Icon = action.icon;
            return (
              <Link
                key={action.href}
                href={action.href}
                className="flex flex-col items-center gap-2.5 group py-1"
              >
                <div className="flex items-center justify-center size-10 rounded-xl bg-muted/60 group-hover:bg-muted transition-colors duration-150">
                  <Icon className="size-4 text-muted-foreground group-hover:text-foreground transition-colors duration-150" />
                </div>
                <span className="text-[10px] font-medium text-muted-foreground/70 group-hover:text-muted-foreground transition-colors duration-150 text-center leading-tight">
                  {action.label}
                </span>
              </Link>
            );
          })}
        </div>
      </CardContent>
    </Card>
  );
}
