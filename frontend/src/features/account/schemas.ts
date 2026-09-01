import { z } from "zod";

export const depositSchema = z.object({
  amount: z
    .number({ error: "Informe um valor" })
    .positive({ message: "Valor deve ser maior que zero" }),
});

export type DepositFormData = z.infer<typeof depositSchema>;
