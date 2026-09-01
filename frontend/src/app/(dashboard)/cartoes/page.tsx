"use client";

import { useCards } from "@/features/cards/hooks/use-cards";
import { CardCarousel } from "@/features/cards/components/card-carousel";
import { CreateCardDialog } from "@/features/cards/components/create-card-dialog";
import { Skeleton } from "@/components/ui/skeleton";
import { CreditCard } from "lucide-react";

export default function CartoesPage() {
  const { data: cards, isLoading } = useCards();

  return (
    <div className="flex flex-col gap-6 p-4 max-w-md mx-auto w-full">
      <div className="flex items-center justify-between">
        <h1 className="text-xl font-bold">Meus Cartões</h1>
        <CreateCardDialog />
      </div>

      {isLoading ? (
        <div className="space-y-4">
          <Skeleton className="w-full aspect-[1.586/1] rounded-2xl" />
          <div className="flex justify-center gap-2">
            <Skeleton className="h-8 w-20" />
          </div>
        </div>
      ) : !cards?.length ? (
        <div className="text-center py-16 space-y-3">
          <div className="flex items-center justify-center size-16 rounded-2xl bg-muted mx-auto">
            <CreditCard className="size-8 text-muted-foreground" />
          </div>
          <p className="text-sm text-muted-foreground">
            Você ainda não tem cartões. <br />
            Crie seu primeiro cartão!
          </p>
        </div>
      ) : (
        <CardCarousel cards={cards} />
      )}
    </div>
  );
}
