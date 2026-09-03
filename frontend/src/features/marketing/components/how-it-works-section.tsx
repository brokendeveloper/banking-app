"use client";

import { motion, useReducedMotion } from "framer-motion";
import { fadeUp, fadeIn, stagger, EASE_OUT_EXPO } from "../lib/motion";

const steps = [
  {
    step: "01",
    title: "Cadastre-se",
    description:
      "Preencha seus dados básicos em menos de 2 minutos. Nenhum papel, nenhuma fila, nenhum agendamento.",
  },
  {
    step: "02",
    title: "Verificação automática",
    description:
      "Nossa análise é instantânea. Aprovação imediata para a maioria dos cadastros, 24h por dia.",
  },
  {
    step: "03",
    title: "Comece a usar",
    description:
      "Pix, cartão virtual e acesso completo disponíveis na hora. Cartão físico em até 5 dias úteis.",
  },
];

export function HowItWorksSection() {
  const shouldAnimate = !useReducedMotion();

  return (
    <section id="how-it-works" className="py-24 md:py-32 border-t border-border/40">
      <div className="max-w-6xl mx-auto px-6">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-16 items-start">
          {/* Left: sticky header */}
          <motion.div
            className="lg:sticky lg:top-24 space-y-5"
            variants={shouldAnimate ? fadeIn : undefined}
            initial={shouldAnimate ? "hidden" : false}
            whileInView="show"
            viewport={{ once: true, margin: "0px 0px -80px 0px" }}
          >
            <p className="text-[11px] font-semibold text-primary uppercase tracking-widest">
              Como funciona
            </p>
            <h2 className="text-3xl md:text-4xl font-semibold tracking-tight leading-snug">
              Conta aberta em{" "}
              <span className="text-muted-foreground/40">minutos.</span>
            </h2>
            <p className="text-sm text-muted-foreground/60 leading-relaxed max-w-sm">
              Sem documentação física, sem visitas a agências. Tudo pelo celular,
              de onde você estiver.
            </p>
          </motion.div>

          {/* Right: steps with stagger */}
          <motion.div
            className="space-y-0 divide-y divide-border/40"
            variants={shouldAnimate ? stagger : undefined}
            initial={shouldAnimate ? "hidden" : false}
            whileInView="show"
            viewport={{ once: true, margin: "0px 0px -60px 0px" }}
          >
            {steps.map((item) => (
              <motion.div
                key={item.step}
                variants={shouldAnimate ? fadeUp : undefined}
                className="py-8 flex items-start gap-6 group"
              >
                <p className="text-xs font-mono text-muted-foreground/30 pt-0.5 shrink-0 w-6">
                  {item.step}
                </p>
                <div className="space-y-2">
                  <h3 className="text-base font-semibold text-foreground/90 group-hover:text-foreground transition-colors duration-150">
                    {item.title}
                  </h3>
                  <p className="text-sm text-muted-foreground/55 leading-relaxed">
                    {item.description}
                  </p>
                </div>
              </motion.div>
            ))}
          </motion.div>
        </div>
      </div>
    </section>
  );
}
