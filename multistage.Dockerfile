FROM maven:3.9.9-eclipse-temurin-23-alpine AS build
WORKDIR /app
COPY . .
RUN mvn clean package

FROM eclipse-temurin:23-jdk
LABEL authors="ccs1201" name="contas-pagar-backend" org.opencontainers.image.authors="https://linkedin.com/in/ccs1201"
VOLUME /tmp
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

#docker build -f multistage.Dockerfile . -t contas-pagar-backend