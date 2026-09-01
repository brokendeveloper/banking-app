import { RegisterForm } from "@/features/auth/components/register-form";
import { Building2 } from "lucide-react";

export default function CadastroPage() {
  return (
    <main className="flex min-h-screen items-center justify-center bg-gradient-to-br from-background via-card to-background px-4 py-12">
      <div className="w-full max-w-md space-y-8">
        {/* Logo */}
        <div className="text-center space-y-2">
          <div className="inline-flex items-center justify-center size-14 rounded-2xl bg-primary/10 ring-1 ring-primary/20 mb-2">
            <Building2 className="size-7 text-primary" />
          </div>
          <h1 className="text-3xl font-bold tracking-tight">Cesar Bank</h1>
          <p className="text-muted-foreground text-sm">
            Crie sua conta bancária digital gratuitamente
          </p>
        </div>

        {/* Card */}
        <div className="rounded-2xl border border-border/50 bg-card/50 backdrop-blur-sm p-8 shadow-xl shadow-black/20">
          <RegisterForm />
        </div>
      </div>
    </main>
  );
}
