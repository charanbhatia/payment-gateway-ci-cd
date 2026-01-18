# Payment Gateway Service

A minimal Spring Boot microservice that implements payment creation, querying, status updates, and refunds. Built to support DevOps CI/CD demonstrations with quality gates and security scanning.

## Features
- REST APIs for payments: create, get by id, list, refund, update status
- Input validation using `jakarta.validation`
- Persistence via Spring Data JPA
- Global exception handling with consistent error responses
- Health and ping endpoints for smoke tests

## API Endpoints
- `GET /api/v1/payments/health` – Health check
- `GET /api/v1/payments/ping` – Ping for smoke tests
- `POST /api/v1/payments` – Create payment
- `GET /api/v1/payments/{id}` – Get payment by id
- `GET /api/v1/payments?merchantId=...` – List payments (optional filter)
- `POST /api/v1/payments/{id}/refund` – Refund a completed payment
- `PUT /api/v1/payments/{id}/status?status=COMPLETED` – Update status

## Quick Start (Local)

### Prerequisites
- Java 17 (Temurin recommended)
- Maven 3.9+ (or use Docker instructions below)

### Run with H2 (no external DB)
```bash
# From project root
export JAVA_HOME=$( /usr/libexec/java_home -v 17 )
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

If `mvnw` is not present, install Maven and run:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### Run with PostgreSQL (production-like)
Set environment variables or update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/paymentdb
spring.datasource.username=postgres
spring.datasource.password=postgres
```
Then:
```bash
mvn spring-boot:run
```

## Build & Test
```bash
# Build
mvn clean package

# Run unit tests
mvn test
```

## Docker
```bash
# Build image
docker build -t payment-gateway:latest .

# Run container
docker run -p 8080:8080 payment-gateway:latest

# Health check
curl -f http://localhost:8080/actuator/health
```

## Kubernetes (Optional)
K8s manifests under `k8s/` provide a basic Deployment, Service, ConfigMap, and Secret.

```bash
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

## Tech Stack
- Java 17, Spring Boot 3.2
- Spring Web, Spring Data JPA, Validation, Actuator
- PostgreSQL (prod), H2 (local/test)

## Notes
- Default profile uses PostgreSQL; for quick run use `local` profile.
- Code quality: Checkstyle config at `checkstyle.xml`.
- Tests: Unit tests for `PaymentService` and `PaymentController`.
