# Leaderboard Backend System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://www.oracle.com/java/)
[![Database](https://img.shields.io/badge/Database-H2-blue.svg)](https://www.h2database.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

---

## 📋 [PROJECT INFO]
* **Project Name**: `leaderboard`
* **One Line Description**: Proyek latihan backend system design untuk membangun sistem papan peringkat (leaderboard) real-time menggunakan Java & Spring Boot.
* **Problem Statement**: Mempelajari bagaimana menangani perhitungan rank jutaan pemain dengan efisien dibanding query agregasi SQL konvensional yang memicu full-table scan.
* **Target Users**: Backend developer yang sedang mempelajari implementasi system design.
* **Learning Reference**: Berdasarkan panduan dari **[System Design One - Leaderboard System Design](https://systemdesign.one/leaderboard-system-design/)**.

---

## ⚡ [FEATURES]
* 🎯 **Proyek Inisiasi**: Kerangka dasar aplikasi backend menggunakan Spring Boot 4.
* 📦 **Persistensi Data**: Konfigurasi H2 In-Memory Database dan Flyway migration.
* 🛠️ **Fokus Latihan**: Eksplorasi implementasi leaderboard (Top N, Rank Lookup, Relative Rank) sesuai materi referensi.

---

## 💻 [TECH STACK]
* **Language**: Java 25
* **Framework**: Spring Boot 4.1.1
* **Database & ORM**: H2 Database, Spring Data JPA, Spring Data JDBC
* **Migration**: Flyway
* **Tools**: Lombok, Maven Wrapper (`./mvnw`)

---

## 🏗️ [ARCHITECTURE]

### Konsep Dasar (Dari Referensi)
Berdasarkan materi yang dipelajari, sistem dirancang untuk memisahkan write path dan read path:
* **In-Memory Cache (Redis Sorted Set)**: Menangani kalkulasi ranking $O(\log N)$ secara real-time.
* **RDBMS (H2 / PostgreSQL)**: Menyimpan data historis dan profil pemain untuk durabilitas.

---

## ⚙️ [INSTALLATION]

### 1. Clone Repo
```bash
git clone https://github.com/faizulmushofa/leaderboard.git
cd leaderboard
```

### 2. Install Dependencies
Pastikan JDK 25 sudah terpasang di sistem:
```bash
./mvnw clean install -DskipTests
```

### 3. Run Dev Command
```bash
./mvnw spring-boot:run
```

---

## 🔑 [ENV VARIABLES]

Saat ini aplikasi menggunakan konfigurasi bawaan `application.properties`:

| Nama Properti | Nilai Default | Penjelasan |
| :--- | :--- | :--- |
| `spring.application.name` | `leaderboard` | Nama identitas aplikasi Spring Boot |

---

## 🏃 [RUN COMMANDS]

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

Struktur folder aktual repositori saat ini:

```
leaderboard/
├── pom.xml                                           # Dependensi dan build konfigurasi
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── faizul/
│   │   │           └── leaderboard/
│   │   │               └── LeaderboardApplication.java # Main entrypoint
│   │   └── resources/
│   │       └── application.properties                # Konfigurasi aplikasi
│   └── test/
│       └── java/
│           └── com/
│               └── faizul/
│                   └── leaderboard/
│                       └── LeaderboardApplicationTests.java
```

---

## 📝 [NOTES / DECISIONS]

* **Status Proyek**: Masih tahap inisiasi awal (setup project & dependencies).
* **Fokus Selanjutnya**: Mengembangkan model database, koneksi Redis, serta REST API untuk skor dan ranking.

---

## 📄 [LICENSE]
Didistribusikan di bawah lisensi **MIT**.
