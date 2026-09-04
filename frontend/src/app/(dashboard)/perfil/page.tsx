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
        <Skeleton className="size-12 rounded-xl" />
        <div className="space-y-2">
          <Skeleton className="h-4 w-28" />
          <Skeleton className="h-3 w-40" />
        </div>
      </div>
      <Skeleton className="h-32 w-full rounded-lg" />
      <Skeleton className="h-24 w-full rounded-lg" />
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
      <SheetTrigger render={
        <Button
          variant="outline"
          size="sm"
          className="gap-1.5 text-xs border-border/60 font-medium hover:bg-muted/60 transition-colors duration-150"
        />
      }>
        <Edit2 className="size-3" />
        Editar
      </SheetTrigger>
      <SheetContent>
        <SheetHeader>
          <SheetTitle>Editar perfil</SheetTitle>
          <SheetDescription className="text-muted-foreground/70">
            Atualize seus dados pessoais.
          </SheetDescription>
        </SheetHeader>
        <form onSubmit={handleSubmit((data) => update(data))} className="mt-6 space-y-4">
          <div className="space-y-1.5">
            <Label htmlFor="edit-name" className="text-xs font-medium text-muted-foreground uppercase tracking-wider">Nome</Label>
            <Input
              id="edit-name"
              className="h-10 border-border/60 text-sm"
              placeholder={profile.name}
              {...register("name")}
            />
            {errors.name && <p className="text-xs text-destructive/80">{errors.name.message}</p>}
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="edit-email" className="text-xs font-medium text-muted-foreground uppercase tracking-wider">E-mail</Label>
            <Input
              id="edit-email"
              type="email"
              className="h-10 border-border/60 text-sm"
              placeholder={profile.email}
              {...register("email")}
            />
            {errors.email && <p className="text-xs text-destructive/80">{errors.email.message}</p>}
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="edit-telephone" className="text-xs font-medium text-muted-foreground uppercase tracking-wider">Telefone</Label>
            <Input
              id="edit-telephone"
              className="h-10 border-border/60 text-sm"
              placeholder={profile.telephone}
              {...register("telephone")}
            />
            {errors.telephone && <p className="text-xs text-destructive/80">{errors.telephone.message}</p>}
          </div>
          {errors.root && <p className="text-xs text-destructive/80">{errors.root.message}</p>}
        </form>
        <SheetFooter className="mt-6 gap-2">
          <SheetClose render={
            <Button
              variant="outline"
              className="border-border/60 text-sm flex-1"
            />
          }>
            Cancelar
          </SheetClose>
          <Button
            className="text-sm flex-1 bg-primary hover:bg-primary/90 text-primary-foreground transition-colors duration-150"
            onClick={handleSubmit((data) => update(data))}
            disabled={isPending}
          >
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
    <div className="flex flex-col gap-6 p-6 max-w-2xl mx-auto w-full">
      <h1 className="text-lg font-semibold tracking-tight">Perfil</h1>

      {isLoading ? (
        <ProfileSkeleton />
      ) : profile ? (
        <>
          {/* Header */}
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3.5">
              <Avatar className="size-12 rounded-xl">
                <AvatarFallback className="text-sm font-semibold rounded-xl bg-muted text-foreground/70">
                  {initials}
                </AvatarFallback>
              </Avatar>
              <div>
                <p className="text-sm font-semibold leading-tight">{profile.name}</p>
                <p className="text-xs text-muted-foreground/60 mt-0.5">{profile.email}</p>
              </div>
            </div>
            <EditProfileSheet profile={profile} />
          </div>

          {/* Dados pessoais */}
          <Card className="border-border/50">
            <CardHeader className="pb-2">
              <CardTitle className="text-[10px] font-semibold text-muted-foreground/50 uppercase tracking-widest">
                Dados pessoais
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              {[
                { icon: User, label: "Nome completo", value: profile.name },
                { icon: Mail, label: "E-mail", value: profile.email },
                { icon: CreditCard, label: "CPF", value: formatCPF(profile.cpf) },
                { icon: Phone, label: "Telefone", value: formatPhone(profile.telephone) },
              ].map(({ icon: Icon, label, value }, i, arr) => (
                <div key={label}>
                  <div className="flex items-center gap-3">
                    <Icon className="size-3.5 text-muted-foreground/40 shrink-0" />
                    <div>
                      <p className="text-[10px] text-muted-foreground/50">{label}</p>
                      <p className="text-sm font-medium mt-0.5">{value}</p>
                    </div>
                  </div>
                  {i < arr.length - 1 && <Separator className="mt-3 opacity-40" />}
                </div>
              ))}
            </CardContent>
          </Card>

          {/* Conta */}
          <Card className="border-border/50">
            <CardHeader className="pb-2">
              <CardTitle className="text-[10px] font-semibold text-muted-foreground/50 uppercase tracking-widest">
                Conta corrente
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              <div className="flex items-center gap-3">
                <Building2 className="size-3.5 text-muted-foreground/40 shrink-0" />
                <div>
                  <p className="text-[10px] text-muted-foreground/50">Número da conta</p>
                  <p className="text-sm font-medium font-mono mt-0.5">{profile.account.id}</p>
                </div>
              </div>
              <Separator className="opacity-40" />
              <div className="flex items-center gap-3">
                <Wallet className="size-3.5 text-muted-foreground/40 shrink-0" />
                <div>
                  <p className="text-[10px] text-muted-foreground/50">Saldo disponível</p>
                  <p className="text-sm font-semibold text-primary mt-0.5">{formatCurrency(profile.account.balance)}</p>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Cartões vinculados */}
          {profile.cards.length > 0 && (
            <Card className="border-border/50">
              <CardHeader className="pb-2">
                <CardTitle className="text-[10px] font-semibold text-muted-foreground/50 uppercase tracking-widest">
                  Cartões vinculados ({profile.cards.length})
                </CardTitle>
              </CardHeader>
              <CardContent className="space-y-3">
                {profile.cards.map((card, i) => (
                  <div key={card.id}>
                    {i > 0 && <Separator className="mb-3 opacity-40" />}
                    <div className="flex items-center justify-between">
                      <div>
                        <p className="text-sm font-medium font-mono text-foreground/80">
                          {maskCardNumber(card.cardNumber)}
                        </p>
                        <p className="text-[10px] text-muted-foreground/50 mt-0.5">Val. {card.expiration}</p>
                      </div>
                      <Badge
                        variant="outline"
                        className={card.blocked
                          ? "text-[10px] border-destructive/30 text-destructive/70"
                          : "text-[10px] border-border/50 text-muted-foreground/60"
                        }
                      >
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
            variant="ghost"
            className="w-full gap-2 text-xs text-muted-foreground/60 hover:text-destructive hover:bg-destructive/5 transition-colors duration-150 border border-border/40"
            onClick={handleLogout}
          >
            <LogOut className="size-3.5" />
            Sair da conta
          </Button>
        </>
      ) : null}
    </div>
  );
}
