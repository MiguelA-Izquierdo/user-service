# Usa una imagen base de OpenJDK
FROM openjdk:17-jdk-slim

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app/userService

# Copia solo lo necesario para instalar dependencias primero
COPY pom.xml .
COPY mvnw .
COPY .mvn ./.mvn

# Instala dependencias sin compilar el código
RUN chmod +x mvnw && ./mvnw dependency:resolve

# No copiamos el código fuente, ya que lo montaremos como volumen en el Compose

# Expone el puerto de la aplicación
EXPOSE 8080

# Comando para correr la aplicación en modo desarrollo
CMD ["./mvnw", "spring-boot:run"]
