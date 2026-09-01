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
  { name: "name", label: "Nome completo", placeholder: "Seu nome", type: "text", icon: <User className="size-4" />, autoComplete: "name" },
  { name: "email", label: "E-mail", placeholder: "seu@email.com", type: "email", icon: <Mail className="size-4" />, autoComplete: "email" },
  { name: "cpf", label: "CPF (somente números)", placeholder: "00000000000", type: "text", icon: <CreditCard className="size-4" />, autoComplete: "off" },
  { name: "telephone", label: "Telefone (somente números)", placeholder: "11999999999", type: "tel", icon: <Phone className="size-4" />, autoComplete: "tel" },
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
          <Label htmlFor={field.name} className="text-sm font-medium text-foreground/80">
            {field.label}
          </Label>
          <div className="relative">
            <span className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground">
              {field.icon}
            </span>
            <Input
              id={field.name}
              type={field.type}
              placeholder={field.placeholder}
              autoComplete={field.autoComplete}
              className={cn("pl-10", errors[field.name] && "border-destructive")}
              {...register(field.name)}
            />
          </div>
          {errors[field.name] && (
            <p className="text-xs text-destructive">{errors[field.name]?.message}</p>
          )}
        </div>
      ))}

      <div className="space-y-1.5">
        <Label htmlFor="password" className="text-sm font-medium text-foreground/80">
          Senha
        </Label>
        <div className="relative">
          <Lock className="absolute left-3 top-1/2 -translate-y-1/2 size-4 text-muted-foreground" />
          <Input
            id="password"
            type={showPassword ? "text" : "password"}
            placeholder="Mínimo 6 caracteres"
            autoComplete="new-password"
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

      <Button type="submit" className="w-full font-semibold mt-2" size="lg" disabled={isPending}>
        {isPending ? "Criando conta..." : "Criar conta"}
      </Button>

      <p className="text-center text-sm text-muted-foreground">
        Já tem conta?{" "}
        <Link
          href="/login"
          className="font-semibold text-primary hover:text-primary/80 transition-colors"
        >
          Entrar
        </Link>
      </p>
    </form>
  );
}
