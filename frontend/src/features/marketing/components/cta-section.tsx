import Link from "next/link";
import { ArrowRight } from "lucide-react";
import { Button } from "@/components/ui/button";

export function CtaSection() {
  return (
    <section className="py-24 md:py-32 border-t border-border/40">
      <div className="max-w-6xl mx-auto px-6">
        <div className="relative rounded-2xl border border-border/50 bg-card/30 overflow-hidden px-8 py-16 md:px-16 md:py-20 text-center">
          {/* Glow de fundo */}
          <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 size-96 rounded-full bg-primary/5 blur-3xl pointer-events-none" />

          <div className="relative space-y-8">
            <div className="space-y-4 max-w-xl mx-auto">
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
            </div>

            <div className="flex flex-col sm:flex-row gap-3 justify-center">
              <Button
                className="h-11 px-8 gap-2 bg-primary hover:bg-primary/90 text-primary-foreground text-sm font-medium transition-colors duration-150"
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
            </div>

            <p className="text-xs text-muted-foreground/40">
              Sem custo de abertura · Sem mensalidade · Cancele quando quiser
            </p>
          </div>
        </div>
      </div>
    </section>
  );
}
