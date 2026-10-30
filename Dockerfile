FROM eclipse-temurin:25-jre

WORKDIR /app

COPY target/app.jar app.jar

EXPOSE 7070

ENTRYPOINT ["java", "-jar", "app.jar"]