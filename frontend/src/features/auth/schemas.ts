import { z } from "zod";

export const loginSchema = z.object({
  email: z.string().email({ message: "E-mail inválido" }),
  password: z.string().min(6, { message: "Senha deve ter no mínimo 6 caracteres" }),
});

export const registerSchema = z.object({
  name: z.string().min(2, { message: "Nome deve ter no mínimo 2 caracteres" }).trim(),
  cpf: z
    .string()
    .regex(/^\d{11}$/, { message: "CPF deve ter exatamente 11 dígitos numéricos" }),
  email: z.string().email({ message: "E-mail inválido" }),
  password: z.string().min(6, { message: "Senha deve ter no mínimo 6 caracteres" }),
  telephone: z
    .string()
    .regex(/^\d{10,11}$/, { message: "Telefone deve ter 10 ou 11 dígitos numéricos" }),
});

export type LoginFormData = z.infer<typeof loginSchema>;
export type RegisterFormData = z.infer<typeof registerSchema>;
