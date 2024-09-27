# api-gateway/Dockerfile
FROM gradle:8.10-jdk21-alpine AS builder
WORKDIR /app

# Tüm projeyi kopyalayarak build yap
COPY . .

# Build işlemi
RUN gradle :api-gateway:build -x test

# Çalıştırma aşaması
FROM amazoncorretto:21-alpine
VOLUME /tmp
COPY --from=builder /app/api-gateway/build/libs/api-gateway-0.0.1-SNAPSHOT.jar /app/api-gateway.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app/api-gateway.jar"]