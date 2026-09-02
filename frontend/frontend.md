# FRONTEND.md — Cesar Bank (banking-app)

Documento de contexto para o Claude Code implementar o frontend completo, integrado ao backend já existente. Ler por inteiro antes de gerar qualquer código.

---

## 1. Contexto do projeto

Repo: `banking-app`. Backend Java 17 + Spring Boot + PostgreSQL já pronto e funcional, branch `main`. Frontend será construído na pasta `frontend/` (scaffold Next.js já existe, vazio, na branch `frontend`).

Backend roda em `http://localhost:8080`. CORS já libera `http://localhost:3000`. Autenticação via JWT (Bearer token, retornado no login/register).

**Não alterar nada em `backend/`.** Todo trabalho é isolado em `frontend/`.

---

## 2. Stack obrigatória

- Next.js 16 (App Router) + React 19 + TypeScript strict — já no scaffold
- Tailwind CSS v4 — já no scaffold
- shadcn/ui (`npx shadcn@latest init`, style "new-york", base color neutra, CSS variables habilitadas)
- Skiper UI (`https://skiper-ui.com`) — componentes animados compatíveis com shadcn CLI, instalar via `pnpm dlx shadcn add @skiper-ui/skiperNN`. Ver seção 5 para mapeamento de componentes por tela.
- Framer Motion (`motion`) — dependência da maioria dos componentes Skiper UI
- React Hook Form + Zod — formulários e validação
- TanStack Query — cache, fetch, invalidação de dados do servidor
- Zustand — estado de sessão/autenticação no client
- Axios — client HTTP com interceptors

Gerenciador de pacotes: pnpm.

---

## 3. Autenticação

Fluxo:
1. `POST /api/auth/register` ou `POST /api/auth/login` → retorna `token` (JWT).
2. Guardar token. Preferência: cookie httpOnly setado via Route Handler do Next (`/app/api/auth/route.ts` fazendo proxy) para evitar exposição a XSS. Se optar por simplicidade primeiro (fase 1), usar `localStorage` + Zustand persist, e documentar como débito técnico a migrar para cookie httpOnly antes de produção.
3. Todo request autenticado manda `Authorization: Bearer <token>`.
4. Rotas protegidas: middleware do Next (`middleware.ts`) verificando presença de sessão; redirect para `/login` se ausente.
5. `GET /api/profile/verify` usado para validar token ainda válido ao montar o app (splash/loading inicial).

---

## 4. Contrato de API (fonte da verdade)

Base URL: `NEXT_PUBLIC_API_URL` (env, default `http://localhost:8080`).

### Auth — `/api/auth`
| Método | Rota | Request | Response |
|---|---|---|---|
| POST | `/login` | `{ email, password }` | `{ name, email, token }` |
| POST | `/register` | `{ name, cpf (11 dígitos), email, password (min 6), telephone (10-11 dígitos) }` | `{ name, email, message }` |

### Profile — `/api/profile`
| Método | Rota | Request | Response |
|---|---|---|---|
| GET | `/verify` | — | valida token |
| GET | `` | — | `{ name, email, cpf, telephone, account: { id, balance }, cards: CardResponseDTO[] }` |
| PUT | `` | `{ name?, email?, telephone? }` | `{ name, email, telephone }` |

### Dashboard — `/api/dashboard`
| Método | Rota | Response |
|---|---|---|
| GET | `` | `{ name, email, balance, lastTransactions: TransactionStatementResponseDTO[] }` |

### Account — `/api/account`
| Método | Rota | Request | Response |
|---|---|---|---|
| GET | `/balance` | — | `{ balance }` |
| POST | `/deposit` | `{ amount: decimal > 0 }` | `{ balance, message }` |
| GET | `/statement` | — | `{ transactions: TransactionStatementResponseDTO[] }` |

`TransactionStatementResponseDTO`: `{ type: TransactionType, amount, date, description }`
`TransactionType`: `PIX_SENT | PIX_RECEIVED | BOLETO_PAYMENT | INVESTMENT`

### Cards — `/api/cards`
| Método | Rota | Request | Response |
|---|---|---|---|
| POST | `` | `{ holderName }` | `CardResponseDTO` |
| GET | `` | — | `CardResponseDTO[]` |
| PATCH | `/{id}/block` | — | `{ id, blocked, message }` |
| PATCH | `/{id}/unblock` | — | `{ id, blocked, message }` |

`CardResponseDTO`: `{ id, cardNumber, holderName, expiration, blocked, createdAt }`

### Pix — `/api/pix`
| Método | Rota | Request | Response |
|---|---|---|---|
| POST | `/transfer` | `{ pixKey, pixKeyType: PixKeyType, amount > 0 }` | `PixTransferResponseDTO` |

`PixKeyType`: `EMAIL | CPF | PHONE | RANDOM`
`PixTransferResponseDTO`: `{ senderEmail, receiverEmail, amount, timestamp, status: PixTransactionStatus, description, pixKeyType, pixKey }`
`PixTransactionStatus`: `PENDING | COMPLETED | FAILED | CANCELED`

### Boleto — `/api/boleto`
| Método | Rota | Request | Response |
|---|---|---|---|
| POST | `/pay` | `{ barcode, amount > 0 }` | `BoletoPaymentResponseDTO` |

`BoletoPaymentResponseDTO`: `{ barcode, amount, paymentDate, status: BoletoPaymentStatus, description }`
`BoletoPaymentStatus`: `PENDING | PAID | FAILED`

### Investments — `/api/investments`
| Método | Rota | Request | Response |
|---|---|---|---|
| POST | `` | `{ type: InvestmentType, amount > 0 }` | `InvestmentResponseDTO` |
| GET | `` | — | `InvestmentResponseDTO[]` |
| POST | `/{id}/redeem` | — | `InvestmentResponseDTO` |

`InvestmentType`: `CDB | TESOURO_DIRETO | LCI | LCA | POUPANCA`
`InvestmentResponseDTO`: `{ id, type, amount, investmentDate, expectedReturn, maturityDate, redeemed }`

### Notifications — `/api/notifications`
| Método | Rota | Response |
|---|---|---|
| GET | `` | `NotificationResponseDTO[]` |
| PATCH | `/{id}/read` | `NotificationResponseDTO` |

`NotificationResponseDTO`: `{ id, title, message, createdAt, read }`

---

## 5. Design system e componentes visuais

Mobile-first sempre: construir para 375px primeiro, expandir com breakpoints Tailwind (`sm`, `md`, `lg`) depois. Bottom navigation fixo no mobile (Pix, Cartões, Extrato, Mais), sidebar no desktop (`md:` acima).

Tema: dark mode como padrão (estética banking moderno), toggle light/dark disponível. Paleta neutra + uma cor de destaque (accent) para ações primárias (ex.: verde ou roxo — decidir e manter consistente em todo o app).

### shadcn/ui — componentes base
`Button`, `Input`, `Form`, `Card`, `Dialog`, `Drawer`, `Sheet`, `Tabs`, `Switch`, `Skeleton`, `Avatar`, `Badge`, `Popover`, `Calendar`, `Command`, `Separator`, `Toast`/`Sonner`.

### Skiper UI — componentes animados a integrar
Instalar sob demanda por feature (`pnpm dlx shadcn add @skiper-ui/skiperNN`):

| Tela/Feature | Componente Skiper UI | Uso |
|---|---|---|
| Onboarding / Login-Cadastro | `skiper56` (Devouring details sign in) | tela de login com transição de detalhe |
| Dashboard — saldo | `skiper37` (Animated number) | contador animado do saldo ao carregar |
| Dashboard — destaques/promos | `skiper47` ou `skiper50` (Perspective/Creative carousel) | slideshow de cartões/ofertas no topo do dashboard |
| Cartões | `skiper48` (Card swipe carousel) | navegação entre múltiplos cartões do usuário |
| Cartões — showcase inicial (se aplicável) | `skiper35` (Hover expand) | grid de produtos/planos de cartão, expande on hover/click |
| Navbar geral | `skiper57` ou `skiper75` (Vercel/Apple navbar) | header fixo com blur ao rolar |
| Notificações | `skiper41` (Progressive blur) | efeito de blur progressivo na lista ao rolar |
| Busca (extrato, boleto) | `skiper92` (Vercel Command Search) | busca de transações via `Cmd+K` |
| Navegação de seções (Pix/Boleto/Invest) | `skiper96` (Expandable tabs navigation) | tabs mobile animadas |
| Tooltips (info de taxas, CDB, etc.) | `skiper101` (Custom tooltip) | explicações inline |
| Extrato — accordion por data | `skiper103` (Bouncy accordion) | agrupar transações por dia/mês |
| Lista de investimentos | `skiper104` (Scroll reveal grid cards) | reveal ao rolar |
| Loading inicial do app | `skiper9`/`skiper11` (Stairs/Pixel preloader) | preloader de abertura (opcional, avaliar impacto em UX de app financeiro — pode ser removido se atrasar percepção de performance) |

Atribuição: versão free do Skiper UI exige créditos visíveis (rodapé ou seção "sobre"). Confirmar se há licença Pro antes de remover atribuição.

Todas as integrações Skiper UI devem ser adaptadas ao design system do projeto (cores via CSS variables do tema, não hardcoded).

---

## 6. Estrutura de pastas

```
frontend/
  src/
    app/
      (auth)/
        login/page.tsx
        cadastro/page.tsx
      (dashboard)/
        layout.tsx              # shell com bottom nav / sidebar
        page.tsx                # dashboard home
        extrato/page.tsx
        cartoes/page.tsx
        pix/page.tsx
        boleto/page.tsx
        investimentos/page.tsx
        notificacoes/page.tsx
        perfil/page.tsx
      layout.tsx
      globals.css
    components/
      ui/                        # shadcn
      v1/                        # skiper-ui
      shared/                    # componentes de domínio reutilizáveis
    features/
      auth/
      dashboard/
      account/
      cards/
      pix/
      boleto/
      investments/
      notifications/
      profile/
      # cada feature: components/, hooks/, schemas.ts (zod), types.ts
    lib/
      api/
        client.ts                # axios instance + interceptors
        auth.ts
        account.ts
        cards.ts
        pix.ts
        boleto.ts
        investments.ts
        notifications.ts
        profile.ts
      stores/
        auth-store.ts             # zustand
      utils.ts
    hooks/
      use-auth-guard.ts
    middleware.ts
```

---

## 7. Convenção de branches

Base atual do repo: `main`, `developer`, `frontend`, `front-feature/auth`. Seguir o mesmo padrão.

```
main
 └─ developer
     ├─ front-feature/setup
     ├─ front-feature/auth
     ├─ front-feature/dashboard
     ├─ front-feature/account
     ├─ front-feature/cards
     ├─ front-feature/pix
     ├─ front-feature/boleto
     ├─ front-feature/investments
     ├─ front-feature/notifications
     └─ front-feature/profile
```

Regras:
- Cada branch nasce de `developer` (ou de `frontend`, se `developer` ainda não tiver o scaffold mergeado — verificar antes de começar).
- PR de volta para `developer` ao fim de cada feature, funcional e testada manualmente.
- Merge `developer` → `main` só quando o conjunto de telas do MVP estiver fechado.
- Nunca commitar direto em `main`.

---

## 8. Convenção de commits

Conventional Commits, sempre em português, escopo entre parênteses indicando a feature:

```
feat(front-setup): configura shadcn e tema base do tailwind
feat(front-auth): cria formulário de login com validação zod
feat(front-auth): integra login com POST /api/auth/login
feat(front-auth): adiciona persistência de sessão com zustand
style(front-dashboard): adiciona slideshow de saldo com skiper-ui
feat(front-cards): implementa swipe carousel de cartões
fix(front-account): corrige formatação de valores no extrato
refactor(front-shared): extrai hook useAuthGuard para rotas protegidas
chore(front-deps): instala framer-motion e tanstack-query
```

Commits atômicos: um commit por unidade lógica (um componente, um hook, uma integração de endpoint) — nunca uma feature inteira num commit só. Tipos permitidos: `feat`, `fix`, `refactor`, `style`, `chore`, `test`, `docs`.

---

## 9. Ordem de implementação

1. **Setup** — shadcn init, instalar libs (framer-motion, zod, RHF, tanstack-query, zustand, axios), configurar tema (dark/light), configurar `lib/api/client.ts` com interceptor JWT, estrutura de pastas.
2. **Auth** — telas login/cadastro, validação Zod (CPF 11 dígitos, telefone 10-11 dígitos, senha min 6), integração com `/api/auth`, zustand store, middleware de rota protegida.
3. **Dashboard** — layout shell (bottom nav mobile / sidebar desktop), saldo com animated number, slideshow, últimas transações.
4. **Account** — extrato completo (accordion por data), modal/drawer de depósito.
5. **Cards** — listagem em carousel, criar cartão, block/unblock com confirmação.
6. **Pix** — formulário de transferência (chave + tipo + valor), tela de confirmação, resultado.
7. **Boleto** — formulário (código de barras + valor), confirmação, resultado.
8. **Investments** — listagem, criar investimento por tipo, resgate.
9. **Notifications** — lista, marcar como lida, badge de contagem no header/nav.
10. **Profile** — visualização e edição de dados, listagem de cartões vinculados.

Cada fase = uma branch `front-feature/*`, commits atômicos, PR para `developer` ao final.

---

## 10. Padrões de código

- TypeScript strict, sem `any` — tipar todos os DTOs conforme seção 4.
- Componentes de página (`app/**/page.tsx`) magros — lógica em `features/*/hooks` e `features/*/components`.
- Toda chamada de API via TanStack Query (`useQuery`/`useMutation`), nunca fetch direto dentro de componente.
- Todo formulário: Zod schema em `features/*/schemas.ts` + `useForm` com `zodResolver`.
- Estados de loading: `Skeleton` do shadcn, nunca spinner genérico solto.
- Estados de erro: `Toast`/`Sonner`, mensagem vinda da API quando disponível.
- Valores monetários: formatar sempre em BRL (`Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' })`).
- Datas: `date-fns` com locale `ptBR`.
- Sem CSS-in-JS fora de Tailwind — utilitários primeiro, `cn()` do shadcn para composição condicional.