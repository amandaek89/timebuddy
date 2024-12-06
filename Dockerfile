# Använd en officiell Java 21 JDK som basbild
FROM eclipse-temurin:21-jdk

# Sätt arbetskatalogen i containern
WORKDIR /app

# Kopiera build-filer (JAR-filen och eventuella resurser)
COPY target/timebuddy-0.0.1-SNAPSHOT.jar app.jar

# Sätt miljövariabler för databas- och JWT-konfiguration
ENV DB_HOST=${DB_HOST}
ENV DB_PORT=${DB_PORT}
ENV DB_NAME=${DB_NAME}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV JWT_SECRET=${JWT_SECRET}
ENV JWT_EXPIRATION=${JWT_EXPIRATION}

# Exponera porten applikationen kör på (t.ex. 8080)
EXPOSE 8080

# Kör applikationen
CMD ["java", "-jar", "app.jar"]
