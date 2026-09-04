"use client";

import {
  ArrowLeftRight,
  CreditCard,
  TrendingUp,
  Receipt,
  BarChart3,
  ShieldCheck,
} from "lucide-react";
import { motion, useReducedMotion } from "framer-motion";
import { fadeUp, staggerFast } from "../lib/motion";

const features = [
  {
    icon: ArrowLeftRight,
    title: "Pix instantâneo",
    description:
      "Transferências 24 horas por dia, 7 dias por semana, para qualquer banco. Sem custo, sem limite de horário.",
  },
  {
    icon: CreditCard,
    title: "Cartão sem anuidade",
    description:
      "Cartão físico e virtual sem custo de manutenção. Bloqueie e desbloqueie pelo app em segundos.",
  },
  {
    icon: TrendingUp,
    title: "Invista com facilidade",
    description:
      "CDB, LCI, LCA, Tesouro Direto e Poupança com os melhores rendimentos direto na sua conta.",
  },
  {
    icon: Receipt,
    title: "Boletos sem complicação",
    description:
      "Pague qualquer boleto bancário diretamente pelo app. Rápido, seguro e com comprovante na hora.",
  },
  {
    icon: BarChart3,
    title: "Extrato completo",
    description:
      "Histórico detalhado de todas as movimentações, organizado por data com filtros inteligentes.",
  },
  {
    icon: ShieldCheck,
    title: "Segurança total",
    description:
      "Autenticação em dois fatores, bloqueio imediato de cartão e monitoramento contínuo de fraudes.",
  },
];

export function FeaturesSection() {
  const shouldAnimate = !useReducedMotion();

  return (
    <section id="features" className="py-24 md:py-32">
      <div className="max-w-6xl mx-auto px-6">
        {/* Header */}
        <motion.div
          className="max-w-xl mb-16"
          variants={shouldAnimate ? { hidden: { opacity: 0, y: 24 }, show: { opacity: 1, y: 0, transition: { duration: 0.6, ease: [0.16, 1, 0.3, 1] } } } : undefined}
          initial={shouldAnimate ? "hidden" : false}
          whileInView="show"
          viewport={{ once: true, margin: "0px 0px -80px 0px" }}
        >
          <p className="text-[11px] font-semibold text-primary uppercase tracking-widest mb-4">
            Funcionalidades
          </p>
          <h2 className="text-3xl md:text-4xl font-semibold tracking-tight leading-snug mb-4">
            Tudo que você precisa,{" "}
            <span className="text-muted-foreground/40">em um só lugar.</span>
          </h2>
          <p className="text-sm text-muted-foreground/60 leading-relaxed">
            Do básico ao avançado — gerenciar dinheiro nunca foi tão simples.
          </p>
        </motion.div>

        {/* Grid */}
        <motion.div
          className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-px bg-border/30 rounded-2xl overflow-hidden"
          variants={shouldAnimate ? staggerFast : undefined}
          initial={shouldAnimate ? "hidden" : false}
          whileInView="show"
          viewport={{ once: true, margin: "0px 0px -80px 0px" }}
        >
          {features.map((feature, i) => {
            const Icon = feature.icon;
            return (
              <motion.div
                key={i}
                variants={shouldAnimate ? fadeUp : undefined}
                whileHover={
                  shouldAnimate
                    ? {
                        y: -4,
                        transition: { duration: 0.2, ease: [0.16, 1, 0.3, 1] },
                      }
                    : undefined
                }
                className="group bg-background hover:bg-card/60 transition-colors duration-150 p-8 flex flex-col gap-4"
              >
                <div className="flex items-center justify-center size-9 rounded-lg bg-muted/60 group-hover:bg-muted transition-colors duration-150">
                  <Icon className="size-4 text-muted-foreground/70" />
                </div>
                <div className="space-y-2">
                  <h3 className="text-sm font-semibold text-foreground/90">
                    {feature.title}
                  </h3>
                  <p className="text-xs text-muted-foreground/55 leading-relaxed">
                    {feature.description}
                  </p>
                </div>
              </motion.div>
            );
          })}
        </motion.div>
      </div>
    </section>
  );
}
