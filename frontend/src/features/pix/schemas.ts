import { z } from "zod";

export const pixKeyTypeOptions = ["EMAIL", "CPF", "PHONE", "RANDOM"] as const;

export const pixTransferSchema = z.object({
  pixKeyType: z.enum(pixKeyTypeOptions, { message: "Selecione o tipo de chave Pix" }),
  pixKey: z.string().min(1, { message: "Informe a chave Pix" }),
  amount: z
    .number({ error: "Informe o valor" })
    .positive({ message: "Valor deve ser maior que zero" }),
});

export type PixTransferFormData = z.infer<typeof pixTransferSchema>;
