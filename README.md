# Leaderboard Backend System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL%2017-blue.svg)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Cache-Redis%207-red.svg)](https://redis.io/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

---

## 📋 [PROJECT INFO]
* **Project Name**: `leaderboard`
* **One Line Description**: Proyek latihan backend system design untuk membangun sistem papan peringkat (leaderboard) real-time reaktif menggunakan Java & Spring WebFlux.
* **Problem Statement**: Mempelajari bagaimana menangani perhitungan rank jutaan pemain dengan efisien dibanding query agregasi SQL konvensional yang memicu full-table scan.
* **Target Users**: Backend developer yang sedang mempelajari implementasi system design.
* **Learning Reference**: Berdasarkan panduan dari **[System Design One - Leaderboard System Design](https://systemdesign.one/leaderboard-system-design/)**.

---

## ⚡ [FEATURES]
* 🎯 **Arsitektur Reaktif**: Non-blocking I/O menggunakan Spring WebFlux & R2DBC.
* 📦 **Persistensi Data**: PostgreSQL 17 via Spring Data R2DBC (tanpa Hibernate) & Flyway migration.
* ⚡ **Real-time Ranking**: Redis 7 Sorted Set secara reaktif (`ReactiveRedisTemplate`).
* 🐳 **Container Ready**: Docker Compose untuk orchestrasi PostgreSQL & Redis.

---

## 💻 [TECH STACK]
* **Language**: Java 25
* **Framework**: Spring Boot 4.1.1 (Spring WebFlux)
* **Reactive Database**: PostgreSQL 17, Spring Data R2DBC, `r2dbc-postgresql`
* **Cache & Ranking**: Redis 7, Spring Data Redis Reactive (Lettuce)
* **Migration**: Flyway (driver JDBC `postgresql` runtime)
* **Infrastructure**: Docker Compose
* **Tools**: Lombok, Maven Wrapper (`./mvnw`)

---

## 🏗️ [ARCHITECTURE]

### Konsep Dasar (Dari Referensi)
Pemisahan write path dan read path:
* **Redis Sorted Set (Reactive)**: Menangani kalkulasi ranking $O(\log N)$ secara real-time.
* **PostgreSQL (R2DBC)**: Menyimpan data profil pemain, relasi, dan rekam skor secara asinkron.

---

## ⚙️ [INSTALLATION]

### 1. Clone Repo
```bash
git clone https://github.com/faizulmushofa/leaderboard.git
cd leaderboard
```

### 2. Jalankan Infrastruktur (Docker Compose)
Salin environment file dan jalankan service database & Redis:
```bash
cp .env-example .env
docker compose up -d
```

### 3. Install Dependencies
Pastikan JDK 25 sudah terpasang di sistem:
```bash
./mvnw clean install -DskipTests
```

### 4. Run Dev Command
```bash
./mvnw spring-boot:run
```

---

## 🔑 [ENV VARIABLES]

Dikelola melalui file `.env`:

| Nama Properti | Contoh Nilai | Penjelasan |
| :--- | :--- | :--- |
| `POSTGRES_DB` | `leaderboard_db` | Nama database PostgreSQL |
| `POSTGRES_USER` | `leader_board` | User database PostgreSQL |
| `DB_PASSWORD` | `password123` | Password database PostgreSQL |
| `REDIS_PASSWORD` | `redis123` | Password autentikasi Redis |

---

## 🏃 [RUN COMMANDS]

* **Menjalankan Database & Cache**:
  ```bash
  docker compose up -d
  ```
* **Menjalankan Aplikasi**:
  ```bash
  ./mvnw spring-boot:run
  ```
* **Build Artifact JAR**:
  ```bash
  ./mvnw clean package -DskipTests
  ```
* **Menjalankan Test**:
  ```bash
  ./mvnw test
  ```

---

## 📁 [PROJECT STRUCTURE]

```
leaderboard/
├── compose.yaml                                      # Docker Compose (Postgres & Redis)
├── .env-example                                      # Template variabel lingkungan
├── pom.xml                                           # Konfigurasi dependensi reaktif
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── faizul/
│   │   │           └── leaderboard/
│   │   │               ├── models/                   # Domain entities
│   │   │               ├── repositories/             # R2DBC repositories
│   │   │               ├── services/                 # Reactive business logic
│   │   │               └── LeaderboardApplication.java # Entrypoint
│   │   └── resources/
│   │       ├── application.yaml                      # Konfigurasi aplikasi
│   │       └── db/migration/                         # Skrip migrasi Flyway
│   └── test/
│       └── java/
│           └── com/
│               └── faizul/
│                   └── leaderboard/
│                       └── LeaderboardApplicationTests.java
```

---

## 📝 [NOTES / DECISIONS]

* **Peralihan ke Reactive**: Migrasi dari Spring MVC/JPA ke Spring WebFlux + R2DBC + Redis Reactive untuk efisiensi throughput tinggi non-blocking.
* **Flyway Migration**: Memakai JDBC PostgreSQL runtime khusus fase migrasi skema saat startup.

---

## 📄 [LICENSE]
Didistribusikan di bawah lisensi **MIT**.
