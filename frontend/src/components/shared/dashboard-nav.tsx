"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import {
  LayoutDashboard,
  ArrowLeftRight,
  CreditCard,
  FileText,
  TrendingUp,
  Bell,
  User,
  Receipt,
  LogOut,
} from "lucide-react";
import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import { useAuthStore } from "@/lib/stores/auth-store";
import { useRouter } from "next/navigation";
import { Badge } from "@/components/ui/badge";
import { useNotificationCount } from "@/features/notifications/hooks/use-notifications";

const navItems = [
  { href: "/dashboard", label: "Início", icon: LayoutDashboard },
  { href: "/pix", label: "Pix", icon: ArrowLeftRight },
  { href: "/cartoes", label: "Cartões", icon: CreditCard },
  { href: "/extrato", label: "Extrato", icon: FileText },
  { href: "/boleto", label: "Boleto", icon: Receipt },
  { href: "/investimentos", label: "Invest.", icon: TrendingUp },
  { href: "/notificacoes", label: "Alertas", icon: Bell, showBadge: true },
  { href: "/perfil", label: "Perfil", icon: User },
];

const bottomNavItems = navItems.slice(0, 4);

function NavLink({
  href,
  label,
  icon: Icon,
  showBadge,
  unreadCount,
  variant = "sidebar",
}: {
  href: string;
  label: string;
  icon: React.ElementType;
  showBadge?: boolean;
  unreadCount?: number;
  variant?: "sidebar" | "bottom";
}) {
  const pathname = usePathname();
  const isActive = pathname === href;

  if (variant === "bottom") {
    return (
      <Link
        href={href}
        className={cn(
          "flex flex-col items-center gap-1 px-3 py-2 rounded-lg transition-colors duration-150 min-w-0",
          isActive
            ? "text-primary"
            : "text-muted-foreground hover:text-foreground"
        )}
      >
        <div className="relative">
          <Icon className="size-5" />
          {showBadge && unreadCount && unreadCount > 0 ? (
            <Badge
              variant="destructive"
              className="absolute -top-2 -right-2 size-4 p-0 text-[10px] flex items-center justify-center"
            >
              {unreadCount > 9 ? "9+" : unreadCount}
            </Badge>
          ) : null}
        </div>
        <span className="text-[10px] font-medium truncate">{label}</span>
      </Link>
    );
  }

  return (
    <Link
      href={href}
      className={cn(
        "flex items-center gap-3 px-3 py-2 rounded-lg text-sm transition-colors duration-150",
        isActive
          ? "bg-primary/8 text-primary font-medium"
          : "text-muted-foreground font-normal hover:bg-accent hover:text-foreground"
      )}
    >
      <div className="relative">
        <Icon className="size-4" />
        {showBadge && unreadCount && unreadCount > 0 ? (
          <Badge
            variant="destructive"
            className="absolute -top-2 -right-2 size-4 p-0 text-[10px] flex items-center justify-center"
          >
            {unreadCount > 9 ? "9+" : unreadCount}
          </Badge>
        ) : null}
      </div>
      {label}
    </Link>
  );
}

export function DashboardSidebar() {
  const router = useRouter();
  const clearAuth = useAuthStore((s) => s.clearAuth);
  const user = useAuthStore((s) => s.user);
  const { data: unreadCount } = useNotificationCount();

  const handleLogout = () => {
    clearAuth();
    router.push("/login");
  };

  return (
    <aside className="hidden md:flex flex-col w-56 min-h-screen border-r border-border/50 bg-sidebar p-4 gap-1 sticky top-0">
      {/* Logo */}
      <Link href="/dashboard" className="flex items-center gap-2.5 px-2 py-4 mb-2">
        <span className="text-sm font-semibold tracking-tight text-foreground">Cesar Bank</span>
      </Link>

      {/* Nav */}
      <nav className="flex-1 space-y-0.5">
        {navItems.map((item) => (
          <NavLink
            key={item.href}
            {...item}
            unreadCount={unreadCount}
            variant="sidebar"
          />
        ))}
      </nav>

      {/* User + Logout */}
      <div className="border-t border-border/40 pt-4 space-y-2">
        {user && (
          <div className="px-3 py-1.5">
            <p className="text-xs font-medium truncate text-foreground/80">{user.name}</p>
            <p className="text-xs text-muted-foreground/60 truncate mt-0.5">{user.email}</p>
          </div>
        )}
        <Button
          variant="ghost"
          size="sm"
          className="w-full justify-start gap-2.5 text-xs text-muted-foreground hover:text-destructive hover:bg-destructive/5 transition-colors duration-150"
          onClick={handleLogout}
        >
          <LogOut className="size-3.5" />
          Sair da conta
        </Button>
      </div>
    </aside>
  );
}

export function DashboardBottomNav() {
  const { data: unreadCount } = useNotificationCount();

  return (
    <nav className="md:hidden fixed bottom-0 left-0 right-0 z-50 flex items-center justify-around border-t border-border/40 bg-background/95 backdrop-blur-sm px-2 py-2 safe-area-pb">
      {bottomNavItems.map((item) => (
        <NavLink
          key={item.href}
          {...item}
          unreadCount={unreadCount}
          variant="bottom"
        />
      ))}
      <NavLink href="/notificacoes" label="Alertas" icon={Bell} showBadge unreadCount={unreadCount} variant="bottom" />
    </nav>
  );
}
