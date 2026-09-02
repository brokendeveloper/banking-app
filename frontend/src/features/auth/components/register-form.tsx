"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import Link from "next/link";
import { Eye, EyeOff, Lock, Mail, Phone, User, CreditCard } from "lucide-react";
import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { cn } from "@/lib/utils";
import { registerSchema, type RegisterFormData } from "../schemas";
import { useRegister } from "../hooks/use-register";

interface FieldConfig {
  name: keyof RegisterFormData;
  label: string;
  placeholder: string;
  type: string;
  icon: React.ReactNode;
  autoComplete?: string;
}

const fields: FieldConfig[] = [
  { name: "name", label: "Nome completo", placeholder: "Seu nome", type: "text", icon: <User className="size-3.5" />, autoComplete: "name" },
  { name: "email", label: "E-mail", placeholder: "seu@email.com", type: "email", icon: <Mail className="size-3.5" />, autoComplete: "email" },
  { name: "cpf", label: "CPF (somente números)", placeholder: "00000000000", type: "text", icon: <CreditCard className="size-3.5" />, autoComplete: "off" },
  { name: "telephone", label: "Telefone (somente números)", placeholder: "11999999999", type: "tel", icon: <Phone className="size-3.5" />, autoComplete: "tel" },
];

export function RegisterForm() {
  const [showPassword, setShowPassword] = useState(false);
  const { mutate: register_, isPending } = useRegister();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
  });

  return (
    <form onSubmit={handleSubmit((data) => register_(data))} className="space-y-4">
      {fields.map((field) => (
        <div key={field.name} className="space-y-1.5">
          <Label htmlFor={field.name} className="text-xs font-medium text-muted-foreground uppercase tracking-wider">
            {field.label}
          </Label>
          <div className="relative">
            <span className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground/60">
              {field.icon}
            </span>
            <Input
              id={field.name}
              type={field.type}
              placeholder={field.placeholder}
              autoComplete={field.autoComplete}
              className={cn(
                "pl-9 h-10 bg-card border-border/60 text-sm placeholder:text-muted-foreground/40 focus-visible:border-primary/60 focus-visible:ring-primary/20 transition-colors duration-150",
                errors[field.name] && "border-destructive/60"
              )}
              {...register(field.name)}
            />
          </div>
          {errors[field.name] && (
            <p className="text-xs text-destructive/80">{errors[field.name]?.message}</p>
          )}
        </div>
      ))}

      <div className="space-y-1.5">
        <Label htmlFor="password" className="text-xs font-medium text-muted-foreground uppercase tracking-wider">
          Senha
        </Label>
        <div className="relative">
          <Lock className="absolute left-3 top-1/2 -translate-y-1/2 size-3.5 text-muted-foreground/60" />
          <Input
            id="password"
            type={showPassword ? "text" : "password"}
            placeholder="Mínimo 6 caracteres"
            autoComplete="new-password"
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
          {isPending ? "Criando conta..." : "Criar conta"}
        </Button>
      </div>

      <p className="text-center text-sm text-muted-foreground">
        Já tem conta?{" "}
        <Link
          href="/login"
          className="font-medium text-foreground hover:text-primary transition-colors duration-150"
        >
          Entrar
        </Link>
      </p>
    </form>
  );
}
