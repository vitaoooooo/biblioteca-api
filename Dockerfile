FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/biblioteca-api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
USER 10001
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]
