# --- Stage 1: build ---
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY pom.xml .

# Asegurar permiso de ejecucion (necesario en Railway/Linux)
RUN chmod +x mvnw

# Descarga dependencias primero (cache layer)
RUN ./mvnw dependency:go-offline -q

COPY src src
RUN ./mvnw package -DskipTests -q

# --- Stage 2: runtime ---
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
