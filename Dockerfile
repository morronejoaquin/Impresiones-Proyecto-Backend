# Etapa 1: Construcción (Build)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# Copiamos el pom y descargamos dependencias (optimiza la caché de Docker)
COPY pom.xml .
RUN mvn dependency:go-offline
# Copiamos el código y generamos el jar
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Runtime)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Copiamos solo el JAR resultante de la etapa anterior
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]