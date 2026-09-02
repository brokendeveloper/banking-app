const stats = [
  { value: "R$ 2B+", label: "em transações processadas" },
  { value: "500k+", label: "clientes ativos" },
  { value: "99.98%", label: "de disponibilidade" },
  { value: "R$ 0", label: "de mensalidade" },
];

export function StatsSection() {
  return (
    <section className="border-y border-border/40 bg-card/30">
      <div className="max-w-6xl mx-auto px-6 py-16">
        <div className="grid grid-cols-2 md:grid-cols-4 gap-px bg-border/30 rounded-2xl overflow-hidden">
          {stats.map((stat, i) => (
            <div
              key={i}
              className="bg-background px-6 py-8 flex flex-col gap-1.5"
            >
              <p className="text-3xl md:text-4xl font-semibold tracking-tight text-foreground">
                {stat.value}
              </p>
              <p className="text-xs text-muted-foreground/60 leading-snug">
                {stat.label}
              </p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
