import Link from "next/link";
import { ArrowRight, Star } from "lucide-react";
import { Button } from "@/components/ui/button";

function CardMockup() {
  return (
    <div className="relative w-80 h-72 flex items-center justify-center select-none">
      {/* Glow ambiental */}
      <div className="absolute size-56 rounded-full bg-primary/10 blur-3xl" />

      {/* Cartão de trás */}
      <div
        className="absolute w-64 h-40 rounded-2xl opacity-25"
        style={{
          background: "linear-gradient(135deg, #2a2a2a 0%, #181818 100%)",
          transform: "rotate(-6deg) translateX(12px) translateY(10px)",
        }}
      />

      {/* Cartão principal */}
      <div
        className="relative w-64 h-40 rounded-2xl p-5 overflow-hidden shadow-2xl"
        style={{
          background: "linear-gradient(135deg, #1e1e1e 0%, #0e0e0e 100%)",
          transform: "rotate(3deg)",
        }}
      >
        {/* Textura sutil */}
        <div
          className="absolute inset-0 opacity-[0.03]"
          style={{
            backgroundImage:
              "repeating-linear-gradient(0deg, transparent, transparent 2px, rgba(255,255,255,0.5) 2px, rgba(255,255,255,0.5) 3px)",
            backgroundSize: "100% 4px",
          }}
        />

        <div className="relative h-full flex flex-col justify-between">
          <div className="flex items-start justify-between">
            <p className="text-[9px] tracking-widest uppercase text-white/30 font-medium">
              Cesar Bank
            </p>
            <div className="flex gap-0.5">
              <div className="size-4 rounded-full bg-white/20" />
              <div className="size-4 rounded-full bg-white/10 -ml-1.5" />
            </div>
          </div>

          <div className="space-y-2.5">
            <p className="font-mono text-sm tracking-[0.18em] font-medium text-white/70">
              **** **** **** 4242
            </p>
            <div className="flex items-end justify-between">
              <div>
                <p className="text-[7px] uppercase text-white/30 tracking-wider mb-0.5">
                  Titular
                </p>
                <p className="text-[11px] font-semibold uppercase tracking-wide text-white/75">
                  João Silva
                </p>
              </div>
              <div className="text-right">
                <p className="text-[7px] uppercase text-white/30 tracking-wider mb-0.5">
                  Validade
                </p>
                <p className="text-[11px] font-semibold text-white/75">12/28</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Chip de notificação flutuante */}
      <div
        className="absolute -bottom-2 -right-4 bg-card border border-border/60 rounded-xl px-3 py-2 shadow-lg"
        style={{ transform: "rotate(-2deg)" }}
      >
        <p className="text-[10px] text-muted-foreground/60">Pix recebido</p>
        <p className="text-sm font-semibold text-primary">+ R$ 250,00</p>
      </div>
    </div>
  );
}

export function HeroSection() {
  return (
    <section className="min-h-[calc(100vh-3.5rem)] flex items-center">
      <div className="max-w-6xl mx-auto px-6 py-20 w-full">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">
          {/* Texto */}
          <div className="space-y-10">
            <div className="space-y-6">
              <span className="inline-flex items-center gap-1.5 text-[11px] font-semibold text-primary uppercase tracking-widest border border-primary/25 rounded-full px-3 py-1">
                <span className="inline-block size-1.5 rounded-full bg-primary" />
                Banco digital gratuito
              </span>

              <h1 className="text-5xl md:text-6xl lg:text-[4.25rem] font-semibold tracking-tight leading-[1.05]">
                Finanças simples.
                <br />
                <span className="text-muted-foreground/35">Vida melhor.</span>
              </h1>

              <p className="text-base text-muted-foreground/65 leading-relaxed max-w-md">
                Conta digital, Pix instantâneo, cartão sem anuidade, investimentos e
                muito mais — tudo no seu bolso, sem burocracia e sem tarifas ocultas.
              </p>
            </div>

            <div className="flex flex-col sm:flex-row gap-3">
              <Button
                className="h-11 px-6 gap-2 bg-primary hover:bg-primary/90 text-primary-foreground text-sm font-medium transition-colors duration-150"
                render={<Link href="/cadastro" />}
              >
                Abrir conta grátis
                <ArrowRight className="size-4" />
              </Button>
              <Button
                variant="outline"
                className="h-11 px-6 border-border/50 text-sm font-medium text-muted-foreground hover:text-foreground hover:bg-muted/40 transition-colors duration-150"
                render={<a href="#features" />}
              >
                Ver funcionalidades
              </Button>
            </div>

            {/* Social proof inline */}
            <div className="flex flex-wrap items-center gap-5 pt-1">
              <div className="flex items-center gap-2">
                <div className="flex -space-x-2">
                  {Array.from({ length: 4 }).map((_, i) => (
                    <div
                      key={i}
                      className="size-7 rounded-full border-2 border-background bg-muted/80"
                    />
                  ))}
                </div>
                <p className="text-xs text-muted-foreground/55">+500k clientes</p>
              </div>
              <div className="h-4 w-px bg-border/50" />
              <div className="flex items-center gap-1.5">
                <Star className="size-3 text-primary fill-primary" />
                <p className="text-xs text-muted-foreground/55">4.9 na App Store</p>
              </div>
            </div>
          </div>

          {/* Mockup (apenas desktop) */}
          <div className="hidden lg:flex items-center justify-center">
            <CardMockup />
          </div>
        </div>
      </div>
    </section>
  );
}
