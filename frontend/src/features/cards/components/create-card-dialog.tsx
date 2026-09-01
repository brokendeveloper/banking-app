"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Plus } from "lucide-react";
import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { createCardSchema, type CreateCardFormData } from "../schemas";
import { useCreateCard } from "../hooks/use-cards";

export function CreateCardDialog() {
  const [open, setOpen] = useState(false);
  const { mutate: createCard, isPending } = useCreateCard(() => setOpen(false));

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<CreateCardFormData>({
    resolver: zodResolver(createCardSchema),
  });

  const onSubmit = (data: CreateCardFormData) => {
    createCard(data);
    reset();
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger render={<Button className="gap-2" />}>
        <Plus className="size-4" />
        Novo cartão
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Criar novo cartão</DialogTitle>
          <DialogDescription>
            Informe o nome do titular que aparecerá no cartão.
          </DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="holderName">Nome no cartão</Label>
            <Input
              id="holderName"
              placeholder="SEU NOME"
              className={`uppercase ${errors.holderName ? "border-destructive" : ""}`}
              {...register("holderName")}
            />
            {errors.holderName && (
              <p className="text-xs text-destructive">{errors.holderName.message}</p>
            )}
          </div>
        </form>
        <DialogFooter>
          <Button variant="outline" onClick={() => setOpen(false)}>
            Cancelar
          </Button>
          <Button onClick={handleSubmit(onSubmit)} disabled={isPending}>
            {isPending ? "Criando..." : "Criar cartão"}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
