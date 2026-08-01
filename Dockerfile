FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/internal-mcp-server-0.1.0.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
