# 📒 Cesar Bank

Projeto para cadeira de Programação Orientada a Objeto da Cesar School. O mesmo visa a criação de uma aplicação bancária, desenvolvida com **Java + Spring Boot** no seu backend e **React Native** no frontend. Além disso, o banco de dados escolhido foi **PostgresSQL** rodando em **Docker**.

## 🚀 Tecnologias Utilizadas
- Java 17 + Spring Boot
- PostgreSQL 
- Docker 

<br>

# 🚀 Como rodar o projeto

### ✅ Pré-requisitos gerais

- **Java 17+**
- **Docker** e **Docker Compose**
- **IntelliJ IDEA** (ou outro IDE Java com suporte a Maven)

## 🔧 1. Rodando o Back-End (Java + Spring Boot)

### 📦 Dependências e configuração

1. **Abra o IntelliJ IDEA**
   - Vá em `File > Open` e selecione a pasta `backend`.

2. **Importe o projeto como Maven**
   - Certifique-se de que o arquivo `pom.xml` foi detectado.
   - Caso não, clique com o botão direito no `pom.xml` e selecione **"Add as Maven Project"**.

3. **Sincronize o Maven**
   - O IntelliJ pode fazer isso automaticamente.
   - Se necessário, clique em **"Reload Project"** na aba lateral do Maven.

4. **Configure os containers com Docker**
   - No terminal, dentro da pasta `backend`, execute:

     ```bash
     docker-compose up -d
     ```

   - Isso iniciará os serviços auxiliares, como o banco de dados.

### ▶️ Executando a API

- No IntelliJ, abra a classe principal (normalmente `Application.java`, com a anotação `@SpringBootApplication`) e clique em **Run**.
- A API estará acessível em:

```
http://localhost:8080
```

<br>


