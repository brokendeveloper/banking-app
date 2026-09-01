"use client";

import Link from "next/link";
import { ArrowLeftRight, CreditCard, Receipt, TrendingUp, ArrowDownToLine } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";

const actions = [
  { href: "/pix", label: "Pix", icon: ArrowLeftRight, color: "text-violet-400 bg-violet-400/10" },
  { href: "/boleto", label: "Boleto", icon: Receipt, color: "text-amber-400 bg-amber-400/10" },
  { href: "/investimentos", label: "Investir", icon: TrendingUp, color: "text-emerald-400 bg-emerald-400/10" },
  { href: "/cartoes", label: "Cartões", icon: CreditCard, color: "text-sky-400 bg-sky-400/10" },
  { href: "/extrato", label: "Depósito", icon: ArrowDownToLine, color: "text-pink-400 bg-pink-400/10" },
];

export function QuickActions() {
  return (
    <Card>
      <CardContent className="p-4">
        <p className="text-xs font-semibold text-muted-foreground uppercase tracking-wider mb-3">
          Ações rápidas
        </p>
        <div className="grid grid-cols-5 gap-2">
          {actions.map((action) => {
            const Icon = action.icon;
            return (
              <Link
                key={action.href}
                href={action.href}
                className="flex flex-col items-center gap-2 group"
              >
                <div className={`flex items-center justify-center size-11 rounded-xl ${action.color} transition-transform group-hover:scale-105`}>
                  <Icon className="size-5" />
                </div>
                <span className="text-[10px] font-medium text-muted-foreground group-hover:text-foreground transition-colors text-center leading-tight">
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
