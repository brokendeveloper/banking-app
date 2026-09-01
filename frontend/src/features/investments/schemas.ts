import { z } from "zod";

export const investmentTypes = ["CDB", "TESOURO_DIRETO", "LCI", "LCA", "POUPANCA"] as const;

export const createInvestmentSchema = z.object({
  type: z.enum(investmentTypes, { message: "Selecione o tipo de investimento" }),
  amount: z
    .number({ error: "Informe o valor" })
    .positive({ message: "Valor deve ser maior que zero" }),
});

export type CreateInvestmentFormData = z.infer<typeof createInvestmentSchema>;
