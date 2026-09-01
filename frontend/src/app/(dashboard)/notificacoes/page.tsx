"use client";

import { Bell, BellRing, Check } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import { cn, formatDate } from "@/lib/utils";
import { useNotifications, useMarkNotificationRead } from "@/features/notifications/hooks/use-notifications";

export default function NotificacoesPage() {
  const { data: notifications, isLoading } = useNotifications();
  const { mutate: markRead, isPending } = useMarkNotificationRead();

  const unread = notifications?.filter((n) => !n.read) ?? [];
  const read = notifications?.filter((n) => n.read) ?? [];

  return (
    <div className="flex flex-col gap-6 p-4 max-w-2xl mx-auto w-full">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="flex items-center justify-center size-10 rounded-xl bg-primary/10">
            <Bell className="size-5 text-primary" />
          </div>
          <div>
            <h1 className="text-xl font-bold">Notificações</h1>
            {unread.length > 0 && (
              <p className="text-xs text-muted-foreground">{unread.length} não lida{unread.length > 1 ? "s" : ""}</p>
            )}
          </div>
        </div>
      </div>

      {isLoading ? (
        <div className="space-y-3">
          {Array.from({ length: 4 }).map((_, i) => (
            <Skeleton key={i} className="h-20 rounded-xl" />
          ))}
        </div>
      ) : !notifications?.length ? (
        <div className="text-center py-16 space-y-3">
          <div className="flex items-center justify-center size-16 rounded-2xl bg-muted mx-auto">
            <Bell className="size-8 text-muted-foreground" />
          </div>
          <p className="text-sm text-muted-foreground">Nenhuma notificação.</p>
        </div>
      ) : (
        <div className="space-y-6">
          {unread.length > 0 && (
            <div className="space-y-2">
              <p className="text-xs font-semibold text-muted-foreground uppercase tracking-wider">
                Não lidas
              </p>
              {unread.map((n) => (
                <Card key={n.id} className="border-primary/30 bg-primary/5">
                  <CardContent className="p-4 flex items-start gap-3">
                    <div className="flex items-center justify-center size-8 rounded-full bg-primary/10 mt-0.5 shrink-0">
                      <BellRing className="size-4 text-primary" />
                    </div>
                    <div className="flex-1 min-w-0">
                      <div className="flex items-start justify-between gap-2">
                        <p className="text-sm font-semibold">{n.title}</p>
                        <Badge variant="default" className="text-[10px] shrink-0">Nova</Badge>
                      </div>
                      <p className="text-xs text-muted-foreground mt-0.5 leading-relaxed">{n.message}</p>
                      <p className="text-[10px] text-muted-foreground/60 mt-1.5">{formatDate(n.createdAt, "dd/MM/yyyy HH:mm")}</p>
                    </div>
                    <Button
                      variant="ghost"
                      size="icon"
                      className="size-7 shrink-0"
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
              <p className="text-xs font-semibold text-muted-foreground uppercase tracking-wider">
                Lidas
              </p>
              {read.map((n) => (
                <Card key={n.id} className="opacity-60">
                  <CardContent className="p-4 flex items-start gap-3">
                    <div className="flex items-center justify-center size-8 rounded-full bg-muted mt-0.5 shrink-0">
                      <Bell className="size-4 text-muted-foreground" />
                    </div>
                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-medium">{n.title}</p>
                      <p className="text-xs text-muted-foreground mt-0.5 leading-relaxed">{n.message}</p>
                      <p className="text-[10px] text-muted-foreground/60 mt-1.5">{formatDate(n.createdAt, "dd/MM/yyyy HH:mm")}</p>
                    </div>
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
