"use client";

import { useState } from "react";
import { useRouter } from "next/navigation"; 
import Link from "next/link"; 
import toast from "react-hot-toast"; 
import { authService } from "@/app/modules/auth/service/auth.service";

export default function RegisterPage() {

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [cpf, setCpf] = useState("");
  const [telephone, setTelephone] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const router = useRouter();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);

    toast.promise(
      authService.register({ name, email, password, cpf, telephone }),
      {
        loading: 'Creating account...',
        success: (response) => {
          console.log("Registration successful, name:", response.name, " email:", response.email); 
          router.push("/login");
          return 'Account created successfully! Please log in.'; 
        },
        error: 'Failed to register. Please check your details and try again.',
      }
    ).finally(() => {
      setIsLoading(false);
    });
    };

     return (
   
    <main className="flex min-h-screen items-center justify-center bg-gradient-to-br from-slate-900 via-slate-800 to-indigo-950 px-4">
      
      
      <div className="w-full max-w-md space-y-8 rounded-2xl bg-slate-900/50 p-8 shadow-2xl backdrop-blur-xl border border-slate-700/50">
        
        {/* Header */}
        <div className="text-center">
          <h2 className="text-3xl font-extrabold tracking-tight text-white">Create Account</h2>
          <p className="mt-2 text-sm text-indigo-200/70">Join our secure digital community</p>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="mt-8 space-y-6">
          <div className="space-y-5">

                        {/* Name Field */}
            <div>
              <label className="mb-2 block text-sm font-medium text-slate-300" htmlFor="name">Full Name</label>
              <input
                id="name"
                type="text"
                required
                className="block w-full rounded-xl border border-slate-700/50 bg-slate-900/50 px-4 py-3 text-white placeholder-slate-500 focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-500/50 transition-all"
                placeholder="Your full name"
                value={name}
                onChange={(e) => setName(e.target.value)} 
              />
            </div>

                        {/* Email Field */}
            <div>
              <label className="mb-2 block text-sm font-medium text-slate-300" htmlFor="email">Email Address</label>
              <input
                id="email"
                type="email"
                required
                className="block w-full rounded-xl border border-slate-700/50 bg-slate-900/50 px-4 py-3 text-white placeholder-slate-500 focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-500/50 transition-all"
                placeholder="you@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)} 
              />
            </div>
            
            {/* Password Field */}
            <div>
              <label className="mb-2 block text-sm font-medium text-slate-300" htmlFor="password">Password</label>
              <input
                id="password"
                type="password"
                required
                className="block w-full rounded-xl border border-slate-700/50 bg-slate-900/50 px-4 py-3 text-white placeholder-slate-500 focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-500/50 transition-all"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>

                        {/* cpf Field */}
            <div>
              <label className="mb-2 block text-sm font-medium text-slate-300" htmlFor="cpf">CPF</label>
              <input
                id="cpf"
                type="text"
                required
                className="block w-full rounded-xl border border-slate-700/50 bg-slate-900/50 px-4 py-3 text-white placeholder-slate-500 focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-500/50 transition-all"
                placeholder="000.000.000-00"
                value={cpf}
                onChange={(e) => setCpf(e.target.value)}
              />
            </div>

            {/* Telephone Field */}
            <div>
              <label className="mb-2 block text-sm font-medium text-slate-300" htmlFor="telephone">Telephone</label>
              <input
                id="telephone"
                type="text"
                required
                className="block w-full rounded-xl border border-slate-700/50 bg-slate-900/50 px-4 py-3 text-white placeholder-slate-500 focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-500/50 transition-all"
                placeholder="(00) 00000-0000"
                value={telephone}
                onChange={(e) => setTelephone(e.target.value)}
              />
            </div>

          </div>

          {/* Submit Button */}
          <div className="pt-2">
            <button
              type="submit"
              disabled={isLoading}
              className="flex w-full justify-center rounded-xl bg-indigo-600 px-4 py-3 text-sm font-bold text-white shadow-lg hover:bg-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 focus:ring-offset-slate-900 disabled:opacity-70 disabled:cursor-not-allowed transition-all transform active:scale-[0.98]"
            >
              {isLoading ? "Creating account..." : "Create Account"}
            </button>
          </div>
        </form>

        {/* 3. Link de Navegação Elegante */}
        <p className="text-center text-sm text-slate-400 mt-6">
          Already have an account?{" "}
          <Link href="/login" className="font-semibold text-indigo-400 hover:text-indigo-300 transition-colors">
            Sign in
          </Link>
        </p>

      </div>
    </main>
  );
}