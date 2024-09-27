# customer/Dockerfile
FROM gradle:8.10-jdk21-alpine AS builder
WORKDIR /app

# Tüm projeyi kopyalayarak build yap
COPY . .

# Build işlemi
RUN gradle :customer:build -x test

# Çalıştırma aşaması
FROM amazoncorretto:21-alpine
VOLUME /tmp
COPY --from=builder /app/customer/build/libs/customer-0.0.1-SNAPSHOT.jar /app/customer.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app/customer.jar"]