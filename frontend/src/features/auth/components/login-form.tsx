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
      <div className="space-y-2">
        <Label htmlFor="email" className="text-sm font-medium text-foreground/80">
          E-mail
        </Label>
        <div className="relative">
          <Mail className="absolute left-3 top-1/2 -translate-y-1/2 size-4 text-muted-foreground" />
          <Input
            id="email"
            type="email"
            placeholder="seu@email.com"
            autoComplete="email"
            className={cn("pl-10", errors.email && "border-destructive")}
            {...register("email")}
          />
        </div>
        {errors.email && (
          <p className="text-xs text-destructive">{errors.email.message}</p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="password" className="text-sm font-medium text-foreground/80">
          Senha
        </Label>
        <div className="relative">
          <Lock className="absolute left-3 top-1/2 -translate-y-1/2 size-4 text-muted-foreground" />
          <Input
            id="password"
            type={showPassword ? "text" : "password"}
            placeholder="••••••••"
            autoComplete="current-password"
            className={cn("pl-10 pr-10", errors.password && "border-destructive")}
            {...register("password")}
          />
          <button
            type="button"
            onClick={() => setShowPassword((v) => !v)}
            className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground transition-colors"
          >
            {showPassword ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
          </button>
        </div>
        {errors.password && (
          <p className="text-xs text-destructive">{errors.password.message}</p>
        )}
      </div>

      <Button type="submit" className="w-full font-semibold" size="lg" disabled={isPending}>
        {isPending ? "Entrando..." : "Entrar"}
      </Button>

      <p className="text-center text-sm text-muted-foreground">
        Não tem conta?{" "}
        <Link
          href="/cadastro"
          className="font-semibold text-primary hover:text-primary/80 transition-colors"
        >
          Abrir conta
        </Link>
      </p>
    </form>
  );
}
