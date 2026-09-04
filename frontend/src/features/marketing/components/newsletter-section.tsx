"use client";

import { useState } from "react";
import { motion, AnimatePresence, useReducedMotion } from "framer-motion";
import { CheckCircle, Loader2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useNewsletter } from "../hooks/use-newsletter";
import { fadeUp, fadeIn, stagger, EASE_OUT_EXPO } from "../lib/motion";

export function NewsletterSection() {
  const [email, setEmail] = useState("");
  const shouldAnimate = !useReducedMotion();
  const { mutate, isPending, isSuccess, isError } = useNewsletter();

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!email) return;
    mutate(email);
  }

  return (
    <section className="py-24 md:py-32 border-t border-border/40">
      <div className="max-w-6xl mx-auto px-6">
        <motion.div
          className="max-w-xl mx-auto text-center"
          variants={shouldAnimate ? stagger : undefined}
          initial={shouldAnimate ? "hidden" : false}
          whileInView="show"
          viewport={{ once: true, margin: "0px 0px -80px 0px" }}
        >
          <motion.p
            variants={shouldAnimate ? fadeIn : undefined}
            className="text-[11px] font-semibold text-primary uppercase tracking-widest mb-4"
          >
            Newsletter
          </motion.p>

          <motion.h2
            variants={shouldAnimate ? fadeUp : undefined}
            className="text-3xl md:text-4xl font-semibold tracking-tight leading-snug mb-4"
          >
            Fique por dentro.
          </motion.h2>

          <motion.p
            variants={shouldAnimate ? fadeUp : undefined}
            className="text-sm text-muted-foreground/60 leading-relaxed mb-10"
          >
            Novidades, atualizações e dicas financeiras direto no seu e-mail.
          </motion.p>

          {/* Form / success — independent animation */}
          <div>
            <AnimatePresence mode="wait">
              {isSuccess ? (
                <motion.div
                  key="success"
                  initial={shouldAnimate ? { opacity: 0, y: 12 } : false}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ duration: 0.45, ease: EASE_OUT_EXPO }}
                  className="flex flex-col items-center gap-3"
                >
                  <div className="flex items-center justify-center size-10 rounded-full bg-primary/10">
                    <CheckCircle className="size-5 text-primary" />
                  </div>
                  <p className="text-sm text-muted-foreground/70">
                    Inscrição confirmada. Obrigado!
                  </p>
                </motion.div>
              ) : (
                <motion.form
                  key="form"
                  initial={shouldAnimate ? { opacity: 0, y: 12 } : false}
                  animate={{ opacity: 1, y: 0 }}
                  exit={shouldAnimate ? { opacity: 0, y: -8 } : undefined}
                  transition={{ duration: 0.4, ease: EASE_OUT_EXPO, delay: 0.3 }}
                  onSubmit={handleSubmit}
                  className="flex flex-col sm:flex-row gap-2"
                >
                  <input
                    type="email"
                    required
                    placeholder="seu@email.com.br"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    disabled={isPending}
                    className="flex-1 h-11 rounded-lg border border-border/50 bg-muted/30 px-4 text-sm placeholder:text-muted-foreground/40 focus:outline-none focus:ring-1 focus:ring-primary/50 focus:border-primary/50 transition-colors duration-150 disabled:opacity-50"
                  />
                  <Button
                    type="submit"
                    disabled={isPending || !email}
                    className="h-11 px-6 bg-primary hover:bg-primary/90 text-primary-foreground text-sm font-medium transition-all duration-150 hover:scale-[1.02] disabled:opacity-50 disabled:scale-100 shrink-0"
                  >
                    {isPending ? (
                      <Loader2 className="size-4 animate-spin" />
                    ) : (
                      "Inscrever-se"
                    )}
                  </Button>
                </motion.form>
              )}
            </AnimatePresence>

            {isError && !isSuccess && (
              <motion.p
                initial={shouldAnimate ? { opacity: 0 } : false}
                animate={{ opacity: 1 }}
                transition={{ duration: 0.3 }}
                className="mt-3 text-xs text-destructive/80"
              >
                Não foi possível completar a inscrição. Tente novamente.
              </motion.p>
            )}
          </div>

          <motion.p
            variants={shouldAnimate ? fadeIn : undefined}
            className="mt-6 text-xs text-muted-foreground/40"
          >
            Sem spam. Cancele a qualquer momento.
          </motion.p>
        </motion.div>
      </div>
    </section>
  );
}
