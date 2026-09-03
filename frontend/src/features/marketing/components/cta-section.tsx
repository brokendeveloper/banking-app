"use client";

import Link from "next/link";
import { ArrowRight } from "lucide-react";
import { motion, useReducedMotion } from "framer-motion";
import { Button } from "@/components/ui/button";
import { fadeUp, stagger, EASE_OUT_EXPO } from "../lib/motion";

export function CtaSection() {
  const shouldAnimate = !useReducedMotion();

  return (
    <section className="py-24 md:py-32 border-t border-border/40">
      <div className="max-w-6xl mx-auto px-6">
        <motion.div
          className="relative rounded-2xl border border-border/50 bg-card/30 overflow-hidden px-8 py-16 md:px-16 md:py-20 text-center"
          initial={shouldAnimate ? { opacity: 0, scale: 0.97 } : false}
          whileInView={{ opacity: 1, scale: 1 }}
          viewport={{ once: true, margin: "0px 0px -80px 0px" }}
          transition={{ duration: 0.65, ease: EASE_OUT_EXPO }}
        >
          {/* Animated glow */}
          <motion.div
            className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 size-96 rounded-full bg-primary/5 blur-3xl pointer-events-none"
            animate={shouldAnimate ? { opacity: [0.4, 0.75, 0.4] } : undefined}
            transition={{ duration: 4, repeat: Infinity, ease: "easeInOut" }}
          />

          <motion.div
            className="relative space-y-8"
            variants={shouldAnimate ? stagger : undefined}
            initial={shouldAnimate ? "hidden" : false}
            whileInView="show"
            viewport={{ once: true, margin: "0px 0px -80px 0px" }}
          >
            <motion.div
              variants={shouldAnimate ? fadeUp : undefined}
              className="space-y-4 max-w-xl mx-auto"
            >
              <p className="text-[11px] font-semibold text-primary uppercase tracking-widest">
                Comece agora
              </p>
              <h2 className="text-3xl md:text-5xl font-semibold tracking-tight leading-[1.1]">
                Pronto para assumir o controle?
              </h2>
              <p className="text-sm text-muted-foreground/60 leading-relaxed">
                Junte-se a meio milhão de pessoas que já simplificaram sua vida
                financeira com o Cesar Bank.
              </p>
            </motion.div>

            <motion.div
              variants={shouldAnimate ? fadeUp : undefined}
              className="flex flex-col sm:flex-row gap-3 justify-center"
            >
              <Button
                className="h-11 px-8 gap-2 bg-primary hover:bg-primary/90 text-primary-foreground text-sm font-medium transition-all duration-150 hover:scale-[1.02]"
                render={<Link href="/cadastro" />}
              >
                Abrir conta grátis
                <ArrowRight className="size-4" />
              </Button>
              <Button
                variant="outline"
                className="h-11 px-8 border-border/50 text-sm font-medium text-muted-foreground hover:text-foreground hover:bg-muted/40 transition-colors duration-150"
                render={<Link href="/login" />}
              >
                Já tenho conta
              </Button>
            </motion.div>

            <motion.p
              variants={shouldAnimate ? fadeUp : undefined}
              className="text-xs text-muted-foreground/40"
            >
              Sem custo de abertura · Sem mensalidade · Cancele quando quiser
            </motion.p>
          </motion.div>
        </motion.div>
      </div>
    </section>
  );
}
