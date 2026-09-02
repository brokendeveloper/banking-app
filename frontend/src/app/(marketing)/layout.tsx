import Link from "next/link";
import { Button } from "@/components/ui/button";

export default function MarketingLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="min-h-screen flex flex-col bg-background text-foreground">
      {/* Top nav */}
      <header className="fixed top-0 inset-x-0 z-50 border-b border-border/40 bg-background/90 backdrop-blur-sm">
        <div className="max-w-6xl mx-auto px-6 h-14 flex items-center justify-between">
          <Link href="/" className="text-sm font-semibold tracking-tight">
            Cesar Bank
          </Link>

          <nav className="hidden md:flex items-center gap-6 text-sm text-muted-foreground">
            <Link href="#features" className="hover:text-foreground transition-colors duration-150">
              Funcionalidades
            </Link>
            <Link href="#about" className="hover:text-foreground transition-colors duration-150">
              Sobre
            </Link>
          </nav>

          <div className="flex items-center gap-2">
            <Button
              variant="ghost"
              size="sm"
              className="text-xs text-muted-foreground hover:text-foreground"
              render={<Link href="/login" />}
            >
              Entrar
            </Button>
            <Button
              size="sm"
              className="text-xs bg-primary hover:bg-primary/90 text-primary-foreground transition-colors duration-150"
              render={<Link href="/cadastro" />}
            >
              Abrir conta
            </Button>
          </div>
        </div>
      </header>

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
