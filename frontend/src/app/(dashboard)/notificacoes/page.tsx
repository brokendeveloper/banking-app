"use client";

import { Bell, Check } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { formatDate } from "@/lib/utils";
import { useNotifications, useMarkNotificationRead } from "@/features/notifications/hooks/use-notifications";

export default function NotificacoesPage() {
  const { data: notifications, isLoading } = useNotifications();
  const { mutate: markRead, isPending } = useMarkNotificationRead();

  const unread = notifications?.filter((n) => !n.read) ?? [];
  const read = notifications?.filter((n) => n.read) ?? [];

  return (
    <div className="flex flex-col gap-6 p-6 max-w-2xl mx-auto w-full">
      <div>
        <h1 className="text-lg font-semibold tracking-tight">Notificações</h1>
        {unread.length > 0 && (
          <p className="text-xs text-muted-foreground/60 mt-0.5">
            {unread.length} não lida{unread.length > 1 ? "s" : ""}
          </p>
        )}
      </div>

      {isLoading ? (
        <div className="space-y-3">
          {Array.from({ length: 4 }).map((_, i) => (
            <Skeleton key={i} className="h-16 rounded-lg" />
          ))}
        </div>
      ) : !notifications?.length ? (
        <div className="text-center py-20 space-y-4">
          <div className="flex items-center justify-center size-14 rounded-xl bg-muted/60 mx-auto">
            <Bell className="size-6 text-muted-foreground/50" />
          </div>
          <p className="text-sm text-muted-foreground/60">Nenhuma notificação.</p>
        </div>
      ) : (
        <div className="space-y-8">
          {unread.length > 0 && (
            <div className="space-y-2">
              <p className="text-[10px] font-semibold text-muted-foreground/60 uppercase tracking-widest">
                Não lidas
              </p>
              {unread.map((n) => (
                <Card key={n.id} className="border-border/60 bg-card">
                  <CardContent className="p-4 flex items-start gap-3">
                    <span className="mt-1.5 shrink-0 inline-block size-1.5 rounded-full bg-primary" />
                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-medium leading-snug">{n.title}</p>
                      <p className="text-xs text-muted-foreground/70 mt-1 leading-relaxed">{n.message}</p>
                      <p className="text-[10px] text-muted-foreground/40 mt-1.5">{formatDate(n.createdAt, "dd/MM/yyyy HH:mm")}</p>
                    </div>
                    <Button
                      variant="ghost"
                      size="icon"
                      className="size-7 shrink-0 text-muted-foreground/40 hover:text-muted-foreground hover:bg-transparent transition-colors duration-150"
                      disabled={isPending}
                      onClick={() => markRead(n.id)}
                    >
                      <Check className="size-3.5" />
                    </Button>
                  </CardContent>
                </Card>
              ))}
            </div>
          )}

          {read.length > 0 && (
            <div className="space-y-2">
              <p className="text-[10px] font-semibold text-muted-foreground/40 uppercase tracking-widest">
                Lidas
              </p>
              {read.map((n) => (
                <Card key={n.id} className="border-border/30 opacity-40">
                  <CardContent className="p-4">
                    <p className="text-sm font-medium leading-snug">{n.title}</p>
                    <p className="text-xs text-muted-foreground mt-1 leading-relaxed">{n.message}</p>
                    <p className="text-[10px] text-muted-foreground/60 mt-1.5">{formatDate(n.createdAt, "dd/MM/yyyy HH:mm")}</p>
                  </CardContent>
                </Card>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
