"use client";

import { useEffect, useRef, useState } from "react";
import { motion, useInView, useReducedMotion } from "framer-motion";
import { fadeUp, staggerFast } from "../lib/motion";

const stats = [
  { prefix: "R$ ", target: 2, suffix: "B+", decimals: 0, label: "em transações processadas" },
  { prefix: "", target: 500, suffix: "k+", decimals: 0, label: "clientes ativos" },
  { prefix: "", target: 99.98, suffix: "%", decimals: 2, label: "de disponibilidade" },
  { prefix: "R$ ", target: 0, suffix: "", decimals: 0, label: "de mensalidade" },
];

interface StatCardProps {
  prefix: string;
  target: number;
  suffix: string;
  decimals: number;
  label: string;
  shouldAnimate: boolean;
}

function StatCard({ prefix, target, suffix, decimals, label, shouldAnimate }: StatCardProps) {
  const ref = useRef<HTMLDivElement>(null);
  const inView = useInView(ref, { once: true, margin: "0px 0px -60px 0px" });
  const [value, setValue] = useState(target === 0 ? 0 : 0);
  const startedRef = useRef(false);

  useEffect(() => {
    if (!inView || startedRef.current || !shouldAnimate || target === 0) {
      if (inView) setValue(target);
      return;
    }
    startedRef.current = true;

    const duration = 1400;
    const startTime = performance.now();
    const easeOutExpo = (t: number) => 1 - Math.pow(2, -10 * t);

    const tick = (now: number) => {
      const elapsed = Math.min((now - startTime) / duration, 1);
      setValue(parseFloat((easeOutExpo(elapsed) * target).toFixed(decimals)));
      if (elapsed < 1) requestAnimationFrame(tick);
    };

    requestAnimationFrame(tick);
  }, [inView, target, decimals, shouldAnimate]);

  const display =
    target === 0
      ? "0"
      : decimals > 0
      ? value.toFixed(decimals)
      : Math.round(value).toString();

  return (
    <motion.div
      ref={ref}
      variants={shouldAnimate ? fadeUp : undefined}
      className="bg-background px-6 py-8 flex flex-col gap-1.5"
    >
      <p className="text-3xl md:text-4xl font-semibold tracking-tight text-foreground">
        {prefix}
        {display}
        {suffix}
      </p>
      <p className="text-xs text-muted-foreground/60 leading-snug">{label}</p>
    </motion.div>
  );
}

export function StatsSection() {
  const shouldAnimate = !useReducedMotion();

  return (
    <section className="border-y border-border/40 bg-card/30">
      <div className="max-w-6xl mx-auto px-6 py-16">
        <motion.div
          className="grid grid-cols-2 md:grid-cols-4 gap-px bg-border/30 rounded-2xl overflow-hidden"
          variants={shouldAnimate ? staggerFast : undefined}
          initial={shouldAnimate ? "hidden" : false}
          whileInView="show"
          viewport={{ once: true, margin: "0px 0px -80px 0px" }}
        >
          {stats.map((stat, i) => (
            <StatCard key={i} {...stat} shouldAnimate={shouldAnimate} />
          ))}
        </motion.div>
      </div>
    </section>
  );
}
