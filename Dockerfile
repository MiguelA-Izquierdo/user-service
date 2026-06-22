FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn clean package -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/user-service.jar app.jar
# 8080 = public API · 8081 = Actuator/management (internal only — do not route from the public Ingress)
EXPOSE 8080 8081
ENTRYPOINT ["java", "-jar", "app.jar"]