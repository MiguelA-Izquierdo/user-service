FROM openjdk:17-jdk-slim
WORKDIR /app/userService

COPY . .

RUN ./mvnw clean package -DskipTests

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "target/user-service.jar"]
