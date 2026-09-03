"use client";

import { useMutation } from "@tanstack/react-query";
import { newsletterApi } from "@/lib/api/newsletter";

export function useNewsletter() {
  return useMutation({
    mutationFn: (email: string) => newsletterApi.subscribe(email),
  });
}
