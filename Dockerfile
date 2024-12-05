# Använd en officiell Java 17 JDK som basbild
FROM eclipse-temurin:17-jdk

# Sätt arbetskatalogen i containern
WORKDIR /app

# Kopiera build-filer (JAR-filen och eventuella resurser)
COPY target/timebuddy.jar /app/timebuddy.jar

# Exponera porten applikationen kör på (t.ex. 8080)
EXPOSE 8080

# Kör applikationen
CMD ["java", "-jar", "timebuddy.jar"]
