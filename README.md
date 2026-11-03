# 🏢 RoomBook API

> Application de révision full-stack — Java 21 · Spring Boot 4 · Angular 19  
> Architecture Hexagonale + DDD · JPA Senior · SQL Dynamique · Signals / SignalStore

---

## 🎯 Objectif

Projet de révision pratique pour maîtriser :
- La **conception hexagonale** et le **DDD** (Domain-Driven Design)
- **JPA niveau senior** : Persistence Context, EntityManager, JPQL, DTO projections, lifecycle
- **SQL dynamique** avec StringBuilder et paramètres nommés
- **Angular 19** : standalone components, smart/dumb pattern, Signals, SignalStore, RxJS

---

## 🏗️ Architecture Hexagonale

```
infrastructure  →  application  →  domain
```

**Règle d'or : les dépendances pointent toujours vers l'intérieur. Jamais l'inverse.**

```
src/main/java/coding/roombookapi/
├── domain/
│   ├── booking/        # Aggregate Booking, TimeSlot, BookingStatus
│   ├── room/           # Aggregate Room, Capacity
│   └── shared/         # DomainException (0 import Spring/JPA)
├── application/
│   ├── port/in/        # Use cases (interfaces)
│   ├── port/out/       # Repositories (interfaces)
│   └── service/        # Implémentations des use cases
├── infrastructure/
│   └── adapter/
│       ├── in/web/     # Controllers, DTOs, Mappers
│       └── out/
│           ├── persistence/jpa/
│           └── s3/
└── config/
```

---

## 📋 Règles Métier

| # | Règle | Implémentation |
|---|-------|----------------|
| R1 | `start < end`, durée ≤ 4h | `TimeSlot` — constructeur compact |
| R2 | Pas de chevauchement entre réservations confirmées | `A.start < B.end AND B.start < A.end` |
| R3 | Participants ≤ capacité de la salle | `Capacity.canHost(int attendees)` |
| R4 | Annulation impossible si déjà commencée | `TimeSlot.hasStartedAt(clock.now())` |
| R5 | Capacité ∈ [1, 200] | `Capacity` — constructeur compact |
| R6 | UUID générés par le domaine, jamais par la DB | `BookingId.newId()` |

---

## 🔌 Use Cases (API)

| Use Case | Méthode | Endpoint |
|----------|---------|----------|
| BookRoomUseCase | `POST` | `/api/bookings` |
| CancelBookingUseCase | `PATCH` | `/api/bookings/{id}/cancel` |
| GetBookingQuery | `GET` | `/api/bookings/{id}` |
| SearchBookingsQuery | `GET` | `/api/bookings?roomId=&from=&to=&status=` |
| CreateRoomUseCase | `POST` | `/api/rooms` |
| ListRoomsQuery | `GET` | `/api/rooms` |

---

## 🔄 Flux d'une requête — POST /api/bookings

```
HTTP Request
    ↓
BookingController            [infrastructure — in]
    ↓  @Valid DTO → Command
BookRoomService              [application]
    ↓  charge Room via RoomRepository (port out)
Booking.create()             [domain — 0 Spring]
    ↓  vérifie R1, R3, lance exception si violation
BookingRepositoryJpaAdapter  [infrastructure — out]
    ↓  Booking → BookingJpaEntity
PostgreSQL                   [@Version + contrainte SQL chk_slot]
```

---

## 🛠️ Stack Technique

**Backend**
- Java 21 (records, sealed classes, text blocks)
- Spring Boot 4.1.2 (starter-webmvc, starter-data-jpa)
- Hibernate 7 · Jakarta EE 11
- Flyway (migrations versionnées — `ddl-auto=validate`)
- PostgreSQL 17
- HikariCP (connection pool)

**Frontend** *(à venir)*
- Angular 19 — standalone components
- Signals + SignalStore (NgRx)
- RxJS · smart/dumb pattern

---

## 🚀 Lancer le projet

```bash
# PostgreSQL via Docker
docker run -d \
  --name roombook-postgres \
  --restart always \
  -e POSTGRES_DB=roombook \
  -e POSTGRES_USER=roombook \
  -e POSTGRES_PASSWORD=roombook \
  -p 5432:5432 \
  -v /Users/admin/Desktop/data-volume:/var/lib/postgresql/data \
  postgres:17

# Lancer l'API
./mvnw spring-boot:run
```

L'API démarre sur `http://localhost:8080`

---

## 📐 Principes de conception

- **Make illegal states unrepresentable** — les Value Objects sont toujours valides
- **0 import Spring/JPA dans le domaine** — vérifié par ArchUnit
- **UUID assigné par le domaine** — identité avant persistance
- **`@Version Long`** — verrouillage optimiste, détection "new entity" sans SELECT
- **`readOnly=true`** sur toutes les queries — optimisation Hibernate
- **`Clock` injecté** — jamais `LocalDateTime.now()` dans le domaine