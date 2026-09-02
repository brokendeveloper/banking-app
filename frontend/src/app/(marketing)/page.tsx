/**
 * Landing page — Cesar Bank
 *
 * Estrutura de seções (Revolut-inspired):
 *
 * 1. Hero          — headline forte, CTA primário, mock de app/card
 * 2. Social Proof  — números: usuários, transações, uptime
 * 3. Features      — grid de funcionalidades (Pix, Cartões, Investimentos, Boleto)
 * 4. Product Shot  — screenshot animado do dashboard
 * 5. Testimonials  — depoimentos de clientes
 * 6. CTA Final     — "Abra sua conta em minutos"
 *
 * TODO: implementar cada seção como componente em
 * src/features/marketing/components/
 */

import Link from "next/link";
import { Button } from "@/components/ui/button";
import { ArrowRight } from "lucide-react";

export default function LandingPage() {
  return (
    <div className="flex flex-col">
      {/* ─── Hero ──────────────────────────────────────────────────── */}
      <section className="min-h-[calc(100vh-3.5rem)] flex items-center">
        <div className="max-w-6xl mx-auto px-6 py-24 w-full">
          <div className="max-w-2xl space-y-8">
            <div className="space-y-4">
              <p className="text-xs font-semibold text-primary uppercase tracking-widest">
                Banco digital
              </p>
              <h1 className="text-5xl md:text-6xl font-semibold tracking-tight leading-[1.1]">
                Sua vida financeira,{" "}
                <span className="text-muted-foreground/50">simplificada.</span>
              </h1>
              <p className="text-lg text-muted-foreground/70 leading-relaxed max-w-lg">
                Conta digital, Pix, cartões, investimentos e muito mais — tudo em
                um único lugar, sem taxas escondidas.
              </p>
            </div>

            <div className="flex items-center gap-3">
              <Button
                className="gap-2 bg-primary hover:bg-primary/90 text-primary-foreground transition-colors duration-150"
                render={<Link href="/cadastro" />}
              >
                Abrir conta grátis
                <ArrowRight className="size-4" />
              </Button>
              <Button
                variant="ghost"
                className="text-muted-foreground hover:text-foreground transition-colors duration-150"
                render={<Link href="/login" />}
              >
                Já tenho conta
              </Button>
            </div>
          </div>
        </div>
      </section>

      {/* ─── Seções futuras ────────────────────────────────────────── */}
      {/*
        <SocialProofSection />
        <FeaturesSection />
        <ProductShotSection />
        <TestimonialsSection />
        <CtaSection />
      */}
    </div>
  );
}
