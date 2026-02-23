# --- ESTÁGIO 1: Build com Maven e JDK 21 ---
# Usamos a imagem oficial do Eclipse Temurin 21 com Maven
FROM maven:3.9-eclipse-temurin-21 AS build

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia apenas o pom.xml primeiro para aproveitar o cache do Docker
# Se o pom.xml não mudou, o Docker reutiliza as dependências baixadas
COPY pom.xml .

# Baixa todas as dependências do projeto
RUN mvn dependency:go-offline

# Copia todo o código-fonte do projeto
COPY src ./src

# Compila o projeto e empacota o .jar
# -DskipTests pula a execução dos testes durante o build do Docker
RUN mvn package -DskipTests

# --- ESTÁGIO 2: Runtime com JRE 21 Mínimo ---
# Usamos a imagem JRE (Runtime) 21 baseada em Alpine, que é muito leve
FROM eclipse-temurin:21-jre-alpine

# Define o diretório de trabalho
WORKDIR /app

# Copia o .jar buildado do estágio anterior (build) para o estágio atual
# O nome do artefato é baseado no <artifactId> e <version> do seu pom.xml
COPY --from=build /app/target/API-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta 8080 (porta padrão do Spring Boot)
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
