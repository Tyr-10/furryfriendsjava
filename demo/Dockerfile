# ====== ETAPA 1: Construcción ======
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar solo pom primero para aprovechar cache
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el resto del proyecto
COPY . .

# Empaquetar sin ejecutar tests
RUN mvn clean package -DskipTests



# ====== ETAPA 2: Ejecución ======
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copiar el jar generado
COPY --from=build /app/target/*.jar app.jar

# Render usa esta variable para el puerto
ENV PORT=8080

# Exponer el puerto
EXPOSE 8080

# Ejecutar con el perfil PROD
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
