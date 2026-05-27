FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY app.jar app.jar
COPY application.properties application.properties
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
