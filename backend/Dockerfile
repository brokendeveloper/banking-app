# Estágio 1: Build (Compilação)
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Copia apenas o pom.xml primeiro para aproveitar o cache do Docker
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código fonte e faz o build (ignorando testes, pois eles rodam na pipeline de CI/CD)
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Imagem Final (Execução)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Cria um usuário não-root por questões de segurança (Boa prática!)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copia o .jar gerado no Estágio 1 para esta nova imagem
COPY --from=builder /app/target/*.jar app.jar

# Expõe a porta que o Spring Boot usa
EXPOSE 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]