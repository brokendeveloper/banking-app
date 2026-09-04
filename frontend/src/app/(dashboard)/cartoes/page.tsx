"use client";

import { useCards } from "@/features/cards/hooks/use-cards";
import { CardCarousel } from "@/features/cards/components/card-carousel";
import { CreateCardDialog } from "@/features/cards/components/create-card-dialog";
import { Skeleton } from "@/components/ui/skeleton";
import { CreditCard } from "lucide-react";

export default function CartoesPage() {
  const { data: cards, isLoading } = useCards();

  return (
    <div className="flex flex-col gap-6 p-6 max-w-md mx-auto w-full">
      <div className="flex items-center justify-between">
        <h1 className="text-lg font-semibold tracking-tight">Meus Cartões</h1>
        <CreateCardDialog />
      </div>

      {isLoading ? (
        <div className="space-y-4">
          <Skeleton className="w-full aspect-[1.586/1] rounded-xl" />
          <div className="flex justify-center gap-2">
            <Skeleton className="h-7 w-16" />
          </div>
        </div>
      ) : !cards?.length ? (
        <div className="text-center py-20 space-y-4">
          <div className="flex items-center justify-center size-14 rounded-xl bg-muted/60 mx-auto">
            <CreditCard className="size-6 text-muted-foreground/50" />
          </div>
          <div className="space-y-1">
            <p className="text-sm font-medium text-foreground/70">Nenhum cartão ainda</p>
            <p className="text-xs text-muted-foreground/50">Crie seu primeiro cartão para começar.</p>
          </div>
        </div>
      ) : (
        <CardCarousel cards={cards} />
      )}
    </div>
  );
}
