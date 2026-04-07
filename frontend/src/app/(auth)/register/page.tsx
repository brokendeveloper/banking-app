"use client";

import { useState } from "react";
import { useRouter } from "next/navigation"; 
import { authService } from "@/app/modules/auth/service/auth.service";

export default function RegisterPage() {

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [cpf, setCpf] = useState("");
  const [telephone, setTelephone] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const router = useRouter();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setIsLoading(true);

    try {
      const response = await authService.register({ name, email, password, cpf, telephone });
      console.log("Registration successful, name:", response.name, " email:", response.email);
      alert("Registration successful!");

      router.push("/login");
    } catch (error) {
      console.error("Registration failed:", error);
      setError("Failed to register. Please check your details and try again.");
    } finally {
      setIsLoading(false);
    }
};

    return (
    <main className="flex min-h-screen items-center justify-center bg-slate-900 px-4">
      <div className="w-full max-w-md space-y-8 rounded-xl bg-slate-800 p-8 shadow-2xl">
        
        {/* Header */}
        <div className="text-center">
          <h2 className="text-3xl font-bold tracking-tight text-white">Welcome to our bank</h2>
          <p className="mt-2 text-sm text-slate-400">Create your digital bank account</p>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="mt-8 space-y-6">
          <div className="space-y-4 rounded-md shadow-sm">

                        {/* name Field */}
            <div>
              <label className="sr-only" htmlFor="name">Name</label>
              <input
                id="name"
                type="text"
                required
                className="block w-full rounded-lg border border-slate-700 bg-slate-900 px-3 py-2 text-white placeholder-slate-400 focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500 sm:text-sm"
                placeholder="Your name"
                value={name}
                onChange={(e) => setName(e.target.value)} 
              />
            </div>

                        {/* Email Field */}
            <div>
              <label className="sr-only" htmlFor="email">Email</label>
              <input
                id="email"
                type="email"
                required
                className="block w-full rounded-lg border border-slate-700 bg-slate-900 px-3 py-2 text-white placeholder-slate-400 focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500 sm:text-sm"
                placeholder="Your email"
                value={email}
                onChange={(e) => setEmail(e.target.value)} 
              />
            </div>
            
            {/* CPF Field */}
            <div>
              <label className="sr-only" htmlFor="cpf">CPF</label>
              <input
                id="cpf"
                type="text"
                required
                className="block w-full rounded-lg border border-slate-700 bg-slate-900 px-3 py-2 text-white placeholder-slate-400 focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500 sm:text-sm"
                placeholder="Your CPF"
                value={cpf}
                onChange={(e) => setCpf(e.target.value)} 
              />
            </div>

            {/* Password Field */}
            <div>
              <label className="sr-only" htmlFor="password">Password</label>
              <input
                id="password"
                type="password"
                required
                className="block w-full rounded-lg border border-slate-700 bg-slate-900 px-3 py-2 text-white placeholder-slate-400 focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500 sm:text-sm"
                placeholder="Your password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>

            {/* Telephone Field */}
            <div>
              <label className="sr-only" htmlFor="telephone">Telephone</label>
              <input
                id="telephone"
                type="text"
                required
                className="block w-full rounded-lg border border-slate-700 bg-slate-900 px-3 py-2 text-white placeholder-slate-400 focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500 sm:text-sm"
                placeholder="Your telephone"
                value={telephone}
                onChange={(e) => setTelephone(e.target.value)}
              />
            </div>
          </div>


          {/* Conditional Error Message */}
          {error && (
            <div className="text-sm text-red-500 text-center font-medium">
              {error}
            </div>
          )}

          {/* Submit Button */}
          <div>
            <button
              type="submit"
              disabled={isLoading}
              className="flex w-full justify-center rounded-lg border border-transparent bg-indigo-600 py-2 px-4 text-sm font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:bg-indigo-400 disabled:cursor-not-allowed transition-colors"
            >
              {isLoading ? "Creating account..." : "Create account"}
            </button>
          </div>
        </form>
      </div>
    </main>
  );
}