# Leaderboard System

Latihan backend system design berbasis artikel: [Leaderboard System Design](https://systemdesign.one/leaderboard-system-design/).

## Tech Stack

- Java 25
- Spring Boot
- H2 Database (In-Memory) / Flyway
- Spring Data JPA & JDBC
- Redis (Sorted Sets)

## Instalasi & Menjalankan

### Prasyarat
- JDK 25
- Maven (atau gunakan `./mvnw`)

### Setup
```bash
git clone <repo-url>
cd leaderboard
./mvnw clean install
./mvnw spring-boot:run
```
Aplikasi jalan di `http://localhost:8080`.
