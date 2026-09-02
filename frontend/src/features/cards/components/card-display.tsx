"use client";

import { useState } from "react";
import { Lock, Unlock } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import { cn, maskCardNumber } from "@/lib/utils";
import type { CardResponse } from "@/lib/api/cards";
import { useBlockCard } from "../hooks/use-cards";

interface CardDisplayProps {
  card: CardResponse;
}

function CreditCardVisual({ card }: { card: CardResponse }) {
  return (
    <div
      className={cn(
        "relative w-full aspect-[1.586/1] rounded-xl p-5 overflow-hidden select-none",
        "text-white/90 shadow-md",
        card.blocked
          ? "bg-[#1c1c1e]"
          : "bg-[#1a1a1a]"
      )}
      style={{
        backgroundImage: card.blocked
          ? "none"
          : "linear-gradient(135deg, #1f1f1f 0%, #111111 100%)",
      }}
    >
      {/* Subtle texture lines */}
      <div className="absolute inset-0 opacity-[0.04]"
        style={{
          backgroundImage: "repeating-linear-gradient(0deg, transparent, transparent 2px, rgba(255,255,255,0.5) 2px, rgba(255,255,255,0.5) 3px)",
          backgroundSize: "100% 4px"
        }}
      />

      <div className="relative h-full flex flex-col justify-between">
        <div className="flex items-start justify-between">
          <div className="space-y-0.5">
            <p className="text-[9px] tracking-widest uppercase text-white/30 font-medium">
              Cesar Bank
            </p>
          </div>
          {card.blocked ? (
            <Badge className="text-[9px] font-medium bg-white/10 text-white/50 border-white/10 rounded-md">
              Bloqueado
            </Badge>
          ) : (
            <div className="flex gap-0.5 items-center">
              <div className="size-5 rounded-full bg-white/20" />
              <div className="size-5 rounded-full bg-white/10 -ml-2" />
            </div>
          )}
        </div>

        <div className="space-y-3">
          <p className="font-mono text-base tracking-[0.2em] font-medium text-white/80">
            {maskCardNumber(card.cardNumber)}
          </p>
          <div className="flex items-end justify-between">
            <div>
              <p className="text-[8px] uppercase text-white/30 font-medium tracking-wider mb-0.5">
                Titular
              </p>
              <p className="text-xs font-semibold uppercase tracking-wide truncate max-w-[160px] text-white/80">
                {card.holderName}
              </p>
            </div>
            <div className="text-right">
              <p className="text-[8px] uppercase text-white/30 font-medium tracking-wider mb-0.5">
                Validade
              </p>
              <p className="text-xs font-semibold text-white/80">{card.expiration}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export function CardDisplay({ card }: CardDisplayProps) {
  const [open, setOpen] = useState(false);
  const { mutate: toggleBlock, isPending } = useBlockCard();

  return (
    <div className="space-y-4">
      <CreditCardVisual card={card} />
      <div className="flex items-center justify-between px-1">
        <div>
          <p className="text-sm font-medium text-foreground/80">{card.holderName}</p>
          <p className="text-xs text-muted-foreground/60 mt-0.5">
            {card.blocked ? "Bloqueado" : "Ativo"}
          </p>
        </div>
        <AlertDialog open={open} onOpenChange={setOpen}>
          <Button
            variant="outline"
            size="sm"
            disabled={isPending}
            className="gap-2 text-xs border-border/60 hover:border-border transition-colors duration-150"
            onClick={() => setOpen(true)}
          >
            {card.blocked ? (
              <><Unlock className="size-3" />Desbloquear</>
            ) : (
              <><Lock className="size-3" />Bloquear</>
            )}
          </Button>
          <AlertDialogContent>
            <AlertDialogHeader>
              <AlertDialogTitle>
                {card.blocked ? "Desbloquear cartão?" : "Bloquear cartão?"}
              </AlertDialogTitle>
              <AlertDialogDescription>
                {card.blocked
                  ? "O cartão voltará a funcionar normalmente."
                  : "O cartão ficará temporariamente inativo. Você pode desbloquear a qualquer momento."}
              </AlertDialogDescription>
            </AlertDialogHeader>
            <AlertDialogFooter>
              <AlertDialogCancel>Cancelar</AlertDialogCancel>
              <AlertDialogAction
                onClick={() => {
                  toggleBlock({ id: card.id, blocked: card.blocked });
                  setOpen(false);
                }}
                className={card.blocked ? "" : "bg-destructive hover:bg-destructive/90 text-white"}
              >
                {card.blocked ? "Desbloquear" : "Bloquear"}
              </AlertDialogAction>
            </AlertDialogFooter>
          </AlertDialogContent>
        </AlertDialog>
      </div>
    </div>
  );
}
