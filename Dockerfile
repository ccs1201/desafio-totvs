FROM eclipse-temurin:23-jdk
LABEL authors="ccs1201" name="contas-pagar-backend"
MAINTAINER https://linkedin.com/in/ccs1201
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
