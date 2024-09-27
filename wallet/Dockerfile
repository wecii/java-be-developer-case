# wallet/Dockerfile
FROM gradle:8.10-jdk21-alpine AS builder
WORKDIR /app

# Tüm projeyi kopyalayarak build yap
COPY . .

# Build işlemi
RUN gradle :wallet:build -x test

# Çalıştırma aşaması
FROM amazoncorretto:21-alpine
VOLUME /tmp
COPY --from=builder /app/wallet/build/libs/wallet-0.0.1-SNAPSHOT.jar /app/wallet.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app/wallet.jar"]