# auth/Dockerfile
FROM gradle:8.10-jdk21-alpine AS builder
WORKDIR /app

# Tüm projeyi kopyalayarak build yap
COPY . .

# Build işlemi
RUN gradle :auth:build -x test

# Çalıştırma aşaması
FROM amazoncorretto:21-alpine
VOLUME /tmp
COPY --from=builder /app/auth/build/libs/auth-0.0.1-SNAPSHOT.jar /app/auth.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app/auth.jar"]