# Multi-stage Dockerfile for ProductCatalogAPI (Spring Boot, Maven)
# First stage: build the fat jar using Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace

# copy only what is needed and build
COPY pom.xml .
COPY src ./src

RUN mvn -B -DskipTests package

# Second stage: smaller runtime image
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# Copy the jar built in the previous stage. Pattern matches the Spring Boot jar.
COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
