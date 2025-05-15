FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src/ /app/src/
RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Crear el script de inicio directamente en el Dockerfile
RUN echo '#!/bin/sh\n\
\n\
# Iniciar la aplicación directamente\n\
java -jar app.jar\n\
' > /app/start.sh

RUN chmod +x /app/start.sh

EXPOSE ${PORT:-8082}
CMD ["/app/start.sh"]
