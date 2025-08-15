# syntax=docker/dockerfile:1.6

# Étape 1 : build sur l'architecture de la machine qui construit (ARM sur ton Mac)
FROM --platform=$BUILDPLATFORM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -B -DskipTests package

# Étape 2 : image finale pour la plateforme cible (amd64 pour ECS Fargate)
FROM --platform=$TARGETPLATFORM eclipse-temurin:21-jdk
WORKDIR /app
# le JAR est archi-indépendant : on le copie simplement
COPY --from=build /app/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
