FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/classes ./classes
COPY target/dependency ./dependency

CMD ["java", "-cp", "classes:dependency/*", "com.example.GeneratorApplication"]