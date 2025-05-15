FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src/ /app/src/
RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Agregar script de inicio para verificar la conexión
COPY start.sh /app/start.sh
RUN chmod +x /app/start.sh

EXPOSE ${PORT:-8082}
CMD ["/app/start.sh"]
