import { DashboardSidebar, DashboardBottomNav } from "@/components/shared/dashboard-nav";

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="flex min-h-screen bg-background">
      <DashboardSidebar />
      <main className="flex-1 flex flex-col min-h-screen pb-20 md:pb-0">
        {children}
      </main>
      <DashboardBottomNav />
    </div>
  );
}
