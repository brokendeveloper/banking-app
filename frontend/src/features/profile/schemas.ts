import { z } from "zod";

export const updateProfileSchema = z.object({
  name: z.string().min(2, { message: "Nome deve ter no mínimo 2 caracteres" }).trim().optional().or(z.literal("")),
  email: z.string().email({ message: "E-mail inválido" }).optional().or(z.literal("")),
  telephone: z
    .string()
    .regex(/^\d{10,11}$/, { message: "Telefone deve ter 10 ou 11 dígitos" })
    .optional()
    .or(z.literal("")),
}).refine(
  (data) => (data.name ?? "") !== "" || (data.email ?? "") !== "" || (data.telephone ?? "") !== "",
  { message: "Informe pelo menos um campo para atualizar" }
);

export type UpdateProfileFormData = z.infer<typeof updateProfileSchema>;
