# order/Dockerfile
FROM gradle:8.10-jdk21-alpine AS builder
WORKDIR /app

# Tüm projeyi kopyalayarak build yap
COPY . .

# Build işlemi
RUN gradle :transaction:build -x test

# Çalıştırma aşaması
FROM amazoncorretto:21-alpine
VOLUME /tmp
COPY --from=builder /app/transaction/build/libs/transaction-0.0.1-SNAPSHOT.jar /app/transaction.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app/transaction.jar"]