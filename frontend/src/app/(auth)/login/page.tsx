import { LoginForm } from "@/features/auth/components/login-form";

export default function LoginPage() {
  return (
    <main className="flex min-h-screen items-center justify-center bg-background px-6 py-16">
      <div className="w-full max-w-sm space-y-10">
        {/* Wordmark */}
        <div className="space-y-1">
          <p className="text-xs font-medium text-muted-foreground tracking-widest uppercase">
            Cesar Bank
          </p>
          <h1 className="text-3xl font-semibold tracking-tight text-foreground">
            Bom ter você de volta.
          </h1>
          <p className="text-sm text-muted-foreground">
            Acesse sua conta para continuar.
          </p>
        </div>

        <LoginForm />
      </div>
    </main>
  );
}
