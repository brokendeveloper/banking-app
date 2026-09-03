import { MarketingHeader } from "@/features/marketing/components/marketing-header";

export default function MarketingLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="min-h-screen flex flex-col bg-background text-foreground">
      <MarketingHeader />

      <main className="flex-1 pt-14">{children}</main>

      {/* Footer */}
      <footer className="border-t border-border/40 py-8">
        <div className="max-w-6xl mx-auto px-6 flex flex-col md:flex-row items-center justify-between gap-4">
          <p className="text-xs text-muted-foreground/50">
            © {new Date().getFullYear()} Cesar Bank. Todos os direitos reservados.
          </p>
          <div className="flex items-center gap-4 text-xs text-muted-foreground/50">
            <Link href="#" className="hover:text-muted-foreground transition-colors duration-150">
              Privacidade
            </Link>
            <Link href="#" className="hover:text-muted-foreground transition-colors duration-150">
              Termos
            </Link>
          </div>
        </div>
      </footer>
    </div>
  );
}
