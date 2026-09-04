"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { ArrowDownToLine } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Drawer,
  DrawerClose,
  DrawerContent,
  DrawerDescription,
  DrawerFooter,
  DrawerHeader,
  DrawerTitle,
  DrawerTrigger,
} from "@/components/ui/drawer";
import { depositSchema, type DepositFormData } from "../schemas";
import { useDeposit } from "../hooks/use-account";
import { useState } from "react";

export function DepositDrawer() {
  const [open, setOpen] = useState(false);
  const { mutate: deposit, isPending } = useDeposit(() => setOpen(false));

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<DepositFormData>({
    resolver: zodResolver(depositSchema),
  });

  const onSubmit = (data: DepositFormData) => {
    deposit(data);
    reset();
  };

  return (
    <Drawer open={open} onOpenChange={setOpen}>
      <DrawerTrigger render={<Button className="gap-2" />}>
        <ArrowDownToLine className="size-4" />
        Depositar
      </DrawerTrigger>
      <DrawerContent>
        <DrawerHeader>
          <DrawerTitle>Realizar depósito</DrawerTitle>
          <DrawerDescription>
            Informe o valor que deseja depositar na sua conta.
          </DrawerDescription>
        </DrawerHeader>
        <form onSubmit={handleSubmit(onSubmit)} className="px-4 space-y-4">
          <div className="space-y-2">
            <Label htmlFor="amount">Valor (R$)</Label>
            <Input
              id="amount"
              type="number"
              step="0.01"
              min="0.01"
              placeholder="0,00"
              className={errors.amount ? "border-destructive" : ""}
              {...register("amount", { valueAsNumber: true })}
            />
            {errors.amount && (
              <p className="text-xs text-destructive">{errors.amount.message}</p>
            )}
          </div>
        </form>
        <DrawerFooter>
          <Button
            onClick={handleSubmit(onSubmit)}
            disabled={isPending}
            className="gap-2"
          >
            <ArrowDownToLine className="size-4" />
            {isPending ? "Processando..." : "Confirmar depósito"}
          </Button>
          <DrawerClose render={<Button variant="outline" />}>
            Cancelar
          </DrawerClose>
        </DrawerFooter>
      </DrawerContent>
    </Drawer>
  );
}
