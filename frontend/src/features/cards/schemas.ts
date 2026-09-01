import { z } from "zod";

export const createCardSchema = z.object({
  holderName: z
    .string()
    .min(3, { message: "Nome deve ter no mínimo 3 caracteres" })
    .max(26, { message: "Nome muito longo para o cartão" })
    .trim(),
});

export type CreateCardFormData = z.infer<typeof createCardSchema>;
