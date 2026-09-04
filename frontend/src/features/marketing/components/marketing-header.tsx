"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { Button } from "@/components/ui/button";

export function MarketingHeader() {
  const [scrolled, setScrolled] = useState(false);

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 24);
    window.addEventListener("scroll", onScroll, { passive: true });
    return () => window.removeEventListener("scroll", onScroll);
  }, []);

  return (
    <header
      className={`fixed top-0 inset-x-0 z-50 backdrop-blur-sm transition-all duration-300 border-b ${
        scrolled
          ? "border-border/40 bg-background/95"
          : "border-transparent bg-background/60"
      }`}
    >
      <div className="max-w-6xl mx-auto px-6 h-14 flex items-center justify-between">
        <Link href="/" className="text-sm font-semibold tracking-tight">
          Cesar Bank
        </Link>

        <nav className="hidden md:flex items-center gap-6 text-sm text-muted-foreground">
          <a
            href="#features"
            className="hover:text-foreground transition-colors duration-150"
          >
            Funcionalidades
          </a>
          <a
            href="#how-it-works"
            className="hover:text-foreground transition-colors duration-150"
          >
            Como funciona
          </a>
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
  );
}
