import { RegisterForm } from "@/features/auth/components/register-form";

export default function CadastroPage() {
  return (
    <main className="flex min-h-screen items-center justify-center bg-background px-6 py-16">
      <div className="w-full max-w-sm space-y-10">
        {/* Wordmark */}
        <div className="space-y-1">
          <p className="text-xs font-medium text-muted-foreground tracking-widest uppercase">
            Cesar Bank
          </p>
          <h1 className="text-3xl font-semibold tracking-tight text-foreground">
            Abra sua conta.
          </h1>
          <p className="text-sm text-muted-foreground">
            Gratuito, digital e sem burocracia.
          </p>
        </div>

        <RegisterForm />
      </div>
    </main>
  );
}
