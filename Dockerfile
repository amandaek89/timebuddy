# Använd en officiell Java 21 JDK som basbild
FROM eclipse-temurin:21-jdk

# Sätt arbetskatalogen i containern
WORKDIR /app

# Kopiera build-filer (JAR-filen och eventuella resurser)
COPY target/timebuddy-0.0.1-SNAPSHOT.jar app.jar

# Exponera porten applikationen kör på (t.ex. 8080)
EXPOSE 8080

# Kör applikationen
CMD ["java", "-jar", "app.jar"]
