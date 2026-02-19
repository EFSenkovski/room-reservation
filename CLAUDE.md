# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Room reservation system (room-reservation) built with Spring Boot 4.0.2, Java 25, PostgreSQL 17, and Maven.

## Build & Run Commands

```bash
# Build
mvnw.cmd clean package            # Windows
./mvnw clean package               # Unix

# Run
mvnw.cmd spring-boot:run
./mvnw spring-boot:run

# Tests
mvnw.cmd test                      # all tests
mvnw.cmd -Dtest=ClassName test     # single test class
mvnw.cmd -Dtest=ClassName#method test  # single test method

# Database (requires Docker)
docker compose up -d               # start PostgreSQL
docker compose down                # stop PostgreSQL
```

## Architecture

The project follows **Hexagonal Architecture (Ports & Adapters)** with three layers:

```
com.efsenkovski.reservasalas
├── common/              # Shared enums (ReservationStatus: ACTIVE, CANCELLED, COMPLETED)
├── core/domain/         # Pure domain - no framework dependencies
│   ├── model/           # Domain models (Room, Reservation, User)
│   ├── port/in/         # Input ports (use case interfaces)
│   ├── port/out/        # Output ports (repository interfaces)
│   └── exception/       # Domain exceptions
└── infraadapters/       # Infrastructure layer
    ├── in/api/          # REST controllers + DTOs (input adapters)
    └── out/persistence/ # JPA entities + repositories (output adapters)
```

**Key conventions:**
- Domain models in `core/domain/model/` are framework-free; JPA entities live in `infraadapters/out/persistence/`
- Mappers in each persistence package convert between domain models and JPA entities
- DTOs are Java `record` types
- All entities use an internal `Long id` plus a public-facing `UUID externalId`
- Domain language is English (Room, Reservation, User)

## Database

PostgreSQL 17 via Docker Compose. Hibernate `ddl-auto: update` manages schema automatically — no migration tool.

Connection: `localhost:5432/room_reservation` (user/pass: `room_reservation`/`room_reservation`)
