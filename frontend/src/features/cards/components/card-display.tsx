"use client";

import { useState } from "react";
import { Lock, Unlock, Wifi } from "lucide-react";
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
        "relative w-full aspect-[1.586/1] rounded-2xl p-5 overflow-hidden select-none",
        "bg-gradient-to-br from-primary to-primary/60 text-primary-foreground shadow-xl shadow-primary/30",
        card.blocked && "from-muted-foreground/40 to-muted-foreground/20 shadow-none"
      )}
    >
      <div className="absolute -top-8 -right-8 size-32 rounded-full bg-white/10" />
      <div className="absolute -bottom-8 -left-8 size-28 rounded-full bg-white/5" />

      <div className="relative h-full flex flex-col justify-between">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-1.5">
            <div className="size-7 rounded-full bg-yellow-400/80" />
            <div className="size-7 rounded-full bg-orange-400/60 -ml-3.5" />
          </div>
          {card.blocked ? (
            <Badge variant="destructive" className="text-[10px] font-bold">BLOQUEADO</Badge>
          ) : (
            <Wifi className="size-5 rotate-90 opacity-70" />
          )}
        </div>

        <div className="space-y-3">
          <p className="font-mono text-lg tracking-widest font-semibold opacity-90">
            {maskCardNumber(card.cardNumber)}
          </p>
          <div className="flex items-end justify-between">
            <div>
              <p className="text-[9px] uppercase opacity-60 font-medium">Titular</p>
              <p className="text-sm font-bold uppercase tracking-wide truncate max-w-[160px]">
                {card.holderName}
              </p>
            </div>
            <div className="text-right">
              <p className="text-[9px] uppercase opacity-60 font-medium">Validade</p>
              <p className="text-sm font-bold">{card.expiration}</p>
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
          <p className="text-sm font-medium">{card.holderName}</p>
          <p className="text-xs text-muted-foreground">
            {card.blocked ? "Cartão bloqueado" : "Cartão ativo"}
          </p>
        </div>
        <AlertDialog open={open} onOpenChange={setOpen}>
          <Button
            variant={card.blocked ? "default" : "outline"}
            size="sm"
            disabled={isPending}
            className="gap-2"
            onClick={() => setOpen(true)}
          >
            {card.blocked ? (
              <><Unlock className="size-3.5" />Desbloquear</>
            ) : (
              <><Lock className="size-3.5" />Bloquear</>
            )}
          </Button>
          <AlertDialogContent>
            <AlertDialogHeader>
              <AlertDialogTitle>
                {card.blocked ? "Desbloquear cartão?" : "Bloquear cartão?"}
              </AlertDialogTitle>
              <AlertDialogDescription>
                {card.blocked
                  ? "O cartão voltará a funcionar normalmente para compras e pagamentos."
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
                className={card.blocked ? "" : "bg-destructive hover:bg-destructive/90"}
              >
                {card.blocked ? "Sim, desbloquear" : "Sim, bloquear"}
              </AlertDialogAction>
            </AlertDialogFooter>
          </AlertDialogContent>
        </AlertDialog>
      </div>
    </div>
  );
}
