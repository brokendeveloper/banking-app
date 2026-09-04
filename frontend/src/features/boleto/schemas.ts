import { z } from "zod";

export const boletoPaySchema = z.object({
  barcode: z
    .string()
    .min(44, { message: "Código de barras inválido (mínimo 44 dígitos)" })
    .max(60, { message: "Código de barras inválido (máximo 60 dígitos)" })
    .regex(/^\d+$/, { message: "Código de barras deve conter apenas dígitos" }),
  amount: z
    .number({ error: "Informe o valor" })
    .positive({ message: "Valor deve ser maior que zero" }),
});

export type BoletoPayFormData = z.infer<typeof boletoPaySchema>;
