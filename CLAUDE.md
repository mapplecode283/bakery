## Project Name

**Mooshi Coffee Platform**

Enterprise coffee ordering ecosystem demonstrating microservices architecture.

### Stack

Java 21 · Spring Boot 3.4 · Spring Cloud 2024.0 · Eureka · Spring Cloud Gateway · Kafka · Redis · PostgreSQL 16 · Flyway · Docker Compose · Next.js 14 · TypeScript · Tailwind CSS · React Query · Zustand · JWT · Prometheus · Grafana · Zipkin

---

# Quick Start

```bash
# 1. Infrastructure
docker compose -f infrastructure/docker-compose.yml up -d

# 2. Build all services
mvn clean install -DskipTests -f pom.xml

# 3. Start services (Discovery first, then Gateway, then others)
cd services/discovery-service && mvn spring-boot:run &
cd services/gateway-service && mvn spring-boot:run &
cd services/auth-service && mvn spring-boot:run &
# ... repeat for all 8 business services

# 4. Frontend
cd frontend/mooshi-web && npm install && PORT=3001 npm run dev
```

### URLs

| Service | URL | Auth |
|---------|-----|------|
| Frontend | http://localhost:3001 | — |
| Gateway API | http://localhost:8090 | JWT Bearer |
| Eureka | http://localhost:8761 | — |
| Grafana | http://localhost:3000 | admin / admin |
| Prometheus | http://localhost:9090 | none |
| Zipkin | http://localhost:9411 | — |
| PostgreSQL | localhost:5432 | mooshi / mooshi123 |
| Redis | localhost:6379 | none |
| Kafka | localhost:9092 | none |

---

# Repository Structure

```
mooshi/
├── infrastructure/
│   ├── docker-compose.yml       # Single PG + Redis + Kafka + ZK + Zipkin + Prometheus + Grafana
│   ├── monitoring/
│   │   ├── prometheus/prometheus.yml
│   │   └── grafana/
│   │       ├── dashboards/
│   │       └── datasources/prometheus.yml
│   └── postgres/init/
│       └── 01-create-databases.sql   # Creates all 8 DBs on startup
│
├── frontend/mooshi-web/        # Next.js 14 + TypeScript + Tailwind
│   └── src/
│       ├── app/                # Pages: auth, menu, cart, checkout, orders, profile
│       ├── components/layout/  # Header, Footer
│       ├── services/api.ts     # Axios client with JWT interceptor
│       ├── store/              # Zustand: auth, cart
│       └── types/              # TypeScript interfaces
│
├── services/
│   ├── discovery-service/      # Eureka :8761
│   ├── gateway-service/        # Spring Cloud Gateway :8090 (CORS, JWT filter, rate limit)
│   ├── auth-service/           # :8081 — register, login, JWT, refresh
│   ├── customer-service/       # :8082 — profile, addresses, favorites, loyalty
│   ├── catalog-service/        # :8083 — menu, categories, products, Redis cache
│   ├── order-service/          # :8084 — cart (Redis), orders, lifecycle
│   ├── payment-service/        # :8085 — mock payments (~90% success)
│   ├── notification-service/   # :8086 — email/SMS/push (log only)
│   ├── inventory-service/      # :8087 — ingredients, stock, low-stock alerts
│   └── delivery-service/       # :8088 — mock APIs, future-focused
│
├── shared/
│   ├── common-lib/             # ApiResponse, ErrorResponse, exceptions, global handler
│   ├── event-contracts/        # Kafka event records (9 topics)
│   └── api-contracts/          # Shared DTOs (RegisterRequest, LoginRequest, TokenResponse)
│
├── pom.xml                     # Parent POM (modules + dependency management)
└── CLAUDE.md
```

---

# Infrastructure

### Single PostgreSQL Instance

One container with 8 databases — Database-per-Service pattern:

```
auth_db · customer_db · catalog_db · order_db
payment_db · inventory_db · delivery_db · notification_db
```

All services connect to `localhost:5432`. Init script at `infrastructure/postgres/init/01-create-databases.sql`.

### Kafka Topics

```
user.registered · order.created · order.paid · order.completed
payment.completed · payment.failed · inventory.low
delivery.assigned · delivery.completed
```

kafka-init container creates topics on startup (one-shot, exits after).

### Redis

Used for: catalog cache (JSON serialization), gateway rate limiting, order cart storage (7-day TTL).

---

# Microservices

## Discovery (8761)
Spring Cloud Netflix Eureka. All services register here.

## Gateway (8090)
Spring Cloud Gateway. CORS allowed origins: `localhost:3001`, `localhost:3000`. JWT validation via GlobalFilter. Rate limiting via Redis. Request correlation ID header. Spring Security configured with `.anyExchange().permitAll()` — JwtAuthFilter handles actual auth.

Routes: `/api/auth/**`, `/api/v1/catalog/**`, `/api/v1/customers/**`, `/api/v1/orders/**`, `/api/v1/payments/**`, `/api/v1/notifications/**`, `/api/v1/delivery/**`, `/api/v1/inventory/**`

## Auth (8081)
Tables: `users`, `roles`, `user_roles`, `refresh_tokens`. Flyway: V1–V4. Spring Security + BCrypt + JWT (HMAC-SHA384). Publishes `UserRegisteredEvent` to Kafka.

## Customer (8082)
Tables: `customers`, `customer_addresses`, `favorites`, `loyalty_points`. Consumes `UserRegisteredEvent` → auto-creates customer profile. User ID from `X-User-Id` header (set by gateway).

## Catalog (8083)
Tables: `categories`, `products`, `product_options`, `product_sizes`. Flyway V5 seeds 4 categories + 14 products with sizes and options. Uses DTO pattern (`ProductSummary`, `ProductDetailResponse`) to avoid Hibernate lazy-loading issues. Redis cache with JSON serialization (GenericJackson2JsonRedisSerializer). All entities annotated with `@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})`.

## Order (8084)
Tables: `orders`, `order_items`, `order_status_history`. Order states: CREATED → PAYMENT_PENDING → PAID → PREPARING → READY_FOR_PICKUP → COMPLETED | CANCELLED. Cart stored in Redis (key: `cart:{userId}`). Uses DTOs (`OrderSummary` for list, `OrderResponse` for detail/place/cancel). `order_items.options_json` uses `@JdbcTypeCode(SqlTypes.JSON)`. Publishes `OrderCreatedEvent`, `OrderPaidEvent`, `OrderCompletedEvent`.

## Payment (8085)
Tables: `payments`, `refunds`. Consumes `OrderCreatedEvent` → auto-processes mock payment (~90% success). Publishes `PaymentCompletedEvent` or `PaymentFailedEvent`.

## Notification (8086)
Tables: `notifications`. Consumes `UserRegisteredEvent`, `OrderCreatedEvent`, `PaymentCompletedEvent`, `OrderCompletedEvent` → creates notification records (mock email/SMS/push — logs only).

## Inventory (8087)
Tables: `ingredients`, `inventory_stock`, `inventory_movements`. Seeds 10 ingredients. Consumes `OrderCreatedEvent` → deducts stock. Publishes `InventoryLowEvent` when below threshold.

## Delivery (8088)
Tables: `drivers`, `driver_locations`, `deliveries`. Mock APIs for delivery assignment/tracking. Future-focused.

---

# API Standards

Base path: `/api/v1/*`. Gateway strips no prefix — services expose full path.

```json
{"success": true, "message": "Operation successful", "data": {}}
{"success": false, "message": "Validation failed", "errors": [{"field": "email", "message": "Invalid"}]}
```

Public endpoints: `/api/auth/**`, `/api/v1/catalog/**`. All others require JWT Bearer token.

---

# Frontend

Next.js 14 on **port 3001** (port 3000 used by Grafana). Env: `NEXT_PUBLIC_API_URL=http://localhost:8090/api`.

Pages: `/` (home), `/menu`, `/menu/[id]`, `/cart`, `/checkout`, `/orders`, `/orders/[id]`, `/profile`, `/profile/addresses`, `/profile/favorites`, `/profile/loyalty`, `/profile/notifications`, `/auth/login`, `/auth/register`, `/auth/forgot-password`, `/delivery/login`.

State: Zustand (auth, cart). Server state: React Query. API client: Axios with JWT interceptor + auto-refresh.

---

# Observability

- Micrometer + Prometheus (scraping `/actuator/prometheus` on all 10 services)
- Grafana with auto-provisioned Prometheus datasource
- Zipkin for distributed tracing (Micrometer Tracing + Brave)

---

# Key Design Decisions

- **DTO pattern everywhere** — entities never returned from controllers (avoids lazy-loading issues with `open-in-view=false`)
- **Single PostgreSQL** — simpler ops, still Database-per-Service via separate DBs
- **Gateway port 8090** — avoids conflict with Open WebUI on 8080
- **Frontend port 3001** — avoids conflict with Grafana on 3000
- **Redis JSON serialization** — `GenericJackson2JsonRedisSerializer` for cache (avoids JDK serialization issues)
- **All entities** annotated with `@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})`
- **Gateway security** — Spring Security permits all, JwtAuthFilter handles JWT validation + header injection
- **Kafka init** — topics auto-created by one-shot container, exits after success

---

# Verified API Flow

```
Register → Login → Browse Menu (4 cats, 14 products) → Product Detail
→ Add to Cart (Redis) → Place Order → Payment (Kafka, ~90% success)
→ Order List → Order Detail → Customer Profile
```

All 10 services register with Eureka. All 10 Prometheus targets scrape successfully. Build: `mvn clean install -DskipTests` — 14 modules, ~10s.
