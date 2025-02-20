FROM eclipse-temurin:23-alpine
LABEL authors="ccs1201" name="contas-pagar-backend" org.opencontainers.image.authors="https://linkedin.com/in/ccs1201"
VOLUME /tmp
WORKDIR /app
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
