"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import Link from "next/link";
import { Eye, EyeOff, Lock, Mail } from "lucide-react";
import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { cn } from "@/lib/utils";
import { loginSchema, type LoginFormData } from "../schemas";
import { useLogin } from "../hooks/use-login";

export function LoginForm() {
  const [showPassword, setShowPassword] = useState(false);
  const { mutate: login, isPending } = useLogin();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  });

  return (
    <form onSubmit={handleSubmit((data) => login(data))} className="space-y-5">
      <div className="space-y-1.5">
        <Label htmlFor="email" className="text-xs font-medium text-muted-foreground uppercase tracking-wider">
          E-mail
        </Label>
        <div className="relative">
          <Mail className="absolute left-3 top-1/2 -translate-y-1/2 size-3.5 text-muted-foreground/60" />
          <Input
            id="email"
            type="email"
            placeholder="seu@email.com"
            autoComplete="email"
            className={cn(
              "pl-9 h-10 bg-card border-border/60 text-sm placeholder:text-muted-foreground/40 focus-visible:border-primary/60 focus-visible:ring-primary/20 transition-colors duration-150",
              errors.email && "border-destructive/60"
            )}
            {...register("email")}
          />
        </div>
        {errors.email && (
          <p className="text-xs text-destructive/80">{errors.email.message}</p>
        )}
      </div>

      <div className="space-y-1.5">
        <Label htmlFor="password" className="text-xs font-medium text-muted-foreground uppercase tracking-wider">
          Senha
        </Label>
        <div className="relative">
          <Lock className="absolute left-3 top-1/2 -translate-y-1/2 size-3.5 text-muted-foreground/60" />
          <Input
            id="password"
            type={showPassword ? "text" : "password"}
            placeholder="••••••••"
            autoComplete="current-password"
            className={cn(
              "pl-9 pr-10 h-10 bg-card border-border/60 text-sm placeholder:text-muted-foreground/40 focus-visible:border-primary/60 focus-visible:ring-primary/20 transition-colors duration-150",
              errors.password && "border-destructive/60"
            )}
            {...register("password")}
          />
          <button
            type="button"
            onClick={() => setShowPassword((v) => !v)}
            className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground/50 hover:text-muted-foreground transition-colors duration-150"
          >
            {showPassword ? <EyeOff className="size-3.5" /> : <Eye className="size-3.5" />}
          </button>
        </div>
        {errors.password && (
          <p className="text-xs text-destructive/80">{errors.password.message}</p>
        )}
      </div>

      <div className="pt-1">
        <Button
          type="submit"
          className="w-full h-10 text-sm font-medium bg-primary hover:bg-primary/90 text-primary-foreground transition-colors duration-150"
          disabled={isPending}
        >
          {isPending ? "Entrando..." : "Entrar"}
        </Button>
      </div>

      <p className="text-center text-sm text-muted-foreground">
        Não tem conta?{" "}
        <Link
          href="/cadastro"
          className="font-medium text-foreground hover:text-primary transition-colors duration-150"
        >
          Abrir conta
        </Link>
      </p>
    </form>
  );
}
