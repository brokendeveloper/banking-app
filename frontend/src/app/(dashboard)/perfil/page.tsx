"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { User, Mail, Phone, CreditCard, Edit2, LogOut, Building2, Wallet } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Separator } from "@/components/ui/separator";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import {
  Sheet,
  SheetClose,
  SheetContent,
  SheetDescription,
  SheetFooter,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import { formatCPF, formatPhone, formatCurrency, maskCardNumber } from "@/lib/utils";
import { updateProfileSchema, type UpdateProfileFormData } from "@/features/profile/schemas";
import { useProfile, useUpdateProfile } from "@/features/profile/hooks/use-profile";
import { useAuthStore } from "@/lib/stores/auth-store";
import { useRouter } from "next/navigation";

function ProfileSkeleton() {
  return (
    <div className="space-y-4">
      <div className="flex items-center gap-4">
        <Skeleton className="size-16 rounded-full" />
        <div className="space-y-2">
          <Skeleton className="h-5 w-32" />
          <Skeleton className="h-4 w-48" />
        </div>
      </div>
      <Skeleton className="h-32 w-full rounded-xl" />
      <Skeleton className="h-24 w-full rounded-xl" />
    </div>
  );
}

function EditProfileSheet({ profile }: { profile: { name: string; email: string; telephone: string } }) {
  const [open, setOpen] = useState(false);
  const { mutate: update, isPending } = useUpdateProfile(() => setOpen(false));

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<UpdateProfileFormData>({
    resolver: zodResolver(updateProfileSchema),
    defaultValues: {
      name: profile.name,
      email: profile.email,
      telephone: profile.telephone,
    },
  });

  return (
    <Sheet open={open} onOpenChange={setOpen}>
      <SheetTrigger render={<Button variant="outline" size="sm" className="gap-2" />}>
        <Edit2 className="size-3.5" />
        Editar
      </SheetTrigger>
      <SheetContent>
        <SheetHeader>
          <SheetTitle>Editar perfil</SheetTitle>
          <SheetDescription>
            Deixe em branco os campos que não deseja alterar.
          </SheetDescription>
        </SheetHeader>
        <form onSubmit={handleSubmit((data) => update(data))} className="mt-6 space-y-4">
          <div className="space-y-2">
            <Label htmlFor="edit-name">Nome</Label>
            <Input id="edit-name" placeholder={profile.name} {...register("name")} />
            {errors.name && <p className="text-xs text-destructive">{errors.name.message}</p>}
          </div>
          <div className="space-y-2">
            <Label htmlFor="edit-email">E-mail</Label>
            <Input id="edit-email" type="email" placeholder={profile.email} {...register("email")} />
            {errors.email && <p className="text-xs text-destructive">{errors.email.message}</p>}
          </div>
          <div className="space-y-2">
            <Label htmlFor="edit-telephone">Telefone (somente números)</Label>
            <Input id="edit-telephone" placeholder={profile.telephone} {...register("telephone")} />
            {errors.telephone && <p className="text-xs text-destructive">{errors.telephone.message}</p>}
          </div>
          {errors.root && <p className="text-xs text-destructive">{errors.root.message}</p>}
        </form>
        <SheetFooter className="mt-6">
          <SheetClose render={<Button variant="outline" />}>Cancelar</SheetClose>
          <Button onClick={handleSubmit((data) => update(data))} disabled={isPending}>
            {isPending ? "Salvando..." : "Salvar"}
          </Button>
        </SheetFooter>
      </SheetContent>
    </Sheet>
  );
}

export default function PerfilPage() {
  const { data: profile, isLoading } = useProfile();
  const clearAuth = useAuthStore((s) => s.clearAuth);
  const router = useRouter();

  const handleLogout = () => {
    clearAuth();
    router.push("/login");
  };

  const initials = profile?.name
    .split(" ")
    .slice(0, 2)
    .map((n) => n[0])
    .join("")
    .toUpperCase() ?? "?";

  return (
    <div className="flex flex-col gap-6 p-4 max-w-2xl mx-auto w-full">
      <h1 className="text-xl font-bold">Perfil</h1>

      {isLoading ? (
        <ProfileSkeleton />
      ) : profile ? (
        <>
          {/* Header */}
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-4">
              <Avatar className="size-16">
                <AvatarFallback className="text-lg font-bold bg-primary/10 text-primary">
                  {initials}
                </AvatarFallback>
              </Avatar>
              <div>
                <p className="text-lg font-bold">{profile.name}</p>
                <p className="text-sm text-muted-foreground">{profile.email}</p>
              </div>
            </div>
            <EditProfileSheet profile={profile} />
          </div>

          {/* Dados pessoais */}
          <Card>
            <CardHeader>
              <CardTitle className="text-sm text-muted-foreground uppercase tracking-wider">
                Dados pessoais
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              <div className="flex items-center gap-3">
                <User className="size-4 text-muted-foreground shrink-0" />
                <div>
                  <p className="text-xs text-muted-foreground">Nome completo</p>
                  <p className="text-sm font-medium">{profile.name}</p>
                </div>
              </div>
              <Separator />
              <div className="flex items-center gap-3">
                <Mail className="size-4 text-muted-foreground shrink-0" />
                <div>
                  <p className="text-xs text-muted-foreground">E-mail</p>
                  <p className="text-sm font-medium">{profile.email}</p>
                </div>
              </div>
              <Separator />
              <div className="flex items-center gap-3">
                <CreditCard className="size-4 text-muted-foreground shrink-0" />
                <div>
                  <p className="text-xs text-muted-foreground">CPF</p>
                  <p className="text-sm font-medium">{formatCPF(profile.cpf)}</p>
                </div>
              </div>
              <Separator />
              <div className="flex items-center gap-3">
                <Phone className="size-4 text-muted-foreground shrink-0" />
                <div>
                  <p className="text-xs text-muted-foreground">Telefone</p>
                  <p className="text-sm font-medium">{formatPhone(profile.telephone)}</p>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Conta */}
          <Card>
            <CardHeader>
              <CardTitle className="text-sm text-muted-foreground uppercase tracking-wider">
                Conta corrente
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              <div className="flex items-center gap-3">
                <Building2 className="size-4 text-muted-foreground shrink-0" />
                <div>
                  <p className="text-xs text-muted-foreground">Número da conta</p>
                  <p className="text-sm font-medium font-mono">{profile.account.id}</p>
                </div>
              </div>
              <Separator />
              <div className="flex items-center gap-3">
                <Wallet className="size-4 text-muted-foreground shrink-0" />
                <div>
                  <p className="text-xs text-muted-foreground">Saldo disponível</p>
                  <p className="text-sm font-bold text-primary">{formatCurrency(profile.account.balance)}</p>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Cartões vinculados */}
          {profile.cards.length > 0 && (
            <Card>
              <CardHeader>
                <CardTitle className="text-sm text-muted-foreground uppercase tracking-wider">
                  Cartões vinculados ({profile.cards.length})
                </CardTitle>
              </CardHeader>
              <CardContent className="space-y-3">
                {profile.cards.map((card, i) => (
                  <div key={card.id}>
                    {i > 0 && <Separator className="mb-3" />}
                    <div className="flex items-center justify-between">
                      <div>
                        <p className="text-sm font-medium font-mono">
                          {maskCardNumber(card.cardNumber)}
                        </p>
                        <p className="text-xs text-muted-foreground">Val. {card.expiration}</p>
                      </div>
                      <Badge variant={card.blocked ? "destructive" : "secondary"}>
                        {card.blocked ? "Bloqueado" : "Ativo"}
                      </Badge>
                    </div>
                  </div>
                ))}
              </CardContent>
            </Card>
          )}

          {/* Sair */}
          <Button
            variant="outline"
            className="w-full gap-2 text-destructive border-destructive/30 hover:bg-destructive/10"
            onClick={handleLogout}
          >
            <LogOut className="size-4" />
            Sair da conta
          </Button>
        </>
      ) : null}
    </div>
  );
}
