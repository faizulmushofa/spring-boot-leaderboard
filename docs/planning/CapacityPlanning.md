# Capacity Planning (v1): Real-Time Leaderboard System

Kalkulasi kapasitas sistem papan peringkat reaktif berbasis skema PostgreSQL aktual dan cache ranking Redis Sorted Set.

Dengan target 70,000,000 pemain terdaftar dan 50,000,000 pemain aktif tiap harinya.

---

## 1. User Base & Traffic Estimations

Berdasarkan rasio industri game dengan target pengguna terdaftar, pemain aktif harian (DAU), serta perbandingan Read:Write = 5:1.

| Metric | Calculation | Value |
| :--- | :--- | :--- |
| **Total Registered Users** | Target total pemain terdaftar | **70,000,000 players** |
| **Daily Active Users (DAU - Write)** | Pemain aktif submit skor harian (~71% dari total) | **50,000,000 players/day** |
| **Daily Active Users (DAU - Read)** | Pemain yang melihat papan peringkat | **50,000,000 players/day** |
| **Average Write QPS** | $50,000,000 / 86,400\text{ sec}$ | **~579 QPS** (dibulatkan **600 QPS**) |
| **Peak Write QPS** | $2 \times \text{Average Write QPS}$ | **~1,200 QPS** |
| **Read : Write Ratio** | Standar sistem papan peringkat | **5 : 1** |
| **Average Read QPS** | $579 \times 5$ | **~2,895 QPS** (dibulatkan **3,000 QPS**) |
| **Peak Read QPS** | $2 \times \text{Average Read QPS}$ | **~6,000 QPS** |

---

## 2. Data Size Breakdown (Berdasarkan Skema SQL Proyek)

### A. Tabel `leaderboard` (PostgreSQL)
* `id` (`uuid`): 16 bytes
* `score` (`integer`): 4 bytes
* `created_at` (`timestamp`): 8 bytes
* `game_id` (`uuid`): 16 bytes
* `player_id` (`uuid`): 16 bytes
* PostgreSQL Tuple Header + Padding: ~24 bytes
* **Total payload per row**: **~84 bytes**
* Indeks B-Tree (`pk`, `fk_game`, `fk_player`): ~48 bytes
* **Total fisik per baris di disk**: **~132 bytes** (dibulatkan aman **~140 bytes**)

### B. Tabel `player` (PostgreSQL)
* `id` (`uuid`): 16 bytes
* `name` (`varchar(100)` avg 20 chars): 21 bytes
* `email` (`varchar(255)` avg 30 chars): 31 bytes
* `created_at` (`timestamp`): 8 bytes
* `last_login` (`timestamp` nullable): 8 bytes
* `profile_image` (`varchar(500)` avg 60 chars): 61 bytes
* `location` (`varchar(255)` avg 15 chars): 16 bytes
* `game_id` (`uuid`): 16 bytes
* Header + Padding + Indeks: ~64 bytes
* **Total fisik per profil player**: **~240 bytes**

### C. Redis Sorted Set (Hot Path Ranking)
Struktur per anggota pada key `leaderboard:{game_id}`:
* `member`: `player_id` UUID string 36 bytes (atau 16 bytes raw binary) = 36 bytes
* `score`: 64-bit IEEE floating point = 8 bytes
* SkipList Node + Dict Entry Overhead (jemalloc): ~72 bytes
* **Total memory per record di Redis**: **~116 - 128 bytes**

---

## 3. Memory Capacity (Redis)

Fokus menampung seluruh pemain terdaftar untuk ranking real-time ($O(\log N)$).

| Metric | Calculation | Total |
| :--- | :--- | :--- |
| **Total Player Count** | Kapasitas user terdaftar | **70,000,000 players** |
| **Single Record Size** | `UUID string + score + internal overhead` | **~128 bytes** |
| **Raw Redis Memory** | $70,000,000 \times 128\text{ bytes}$ | **~8.96 GB** |
| **Safety Buffer (Redis Copy-on-Write & Keys)** | $8.96\text{ GB} \times 1.5$ | **~13.44 GB** |
| **Rekomendasi Instance Redis RAM** | Minimum production instance | **16 GB RAM** |

---

## 4. Storage Capacity (PostgreSQL)

### Skenario 1: Snapshot Terkini (Current Score per Player)
Hanya menyimpan skor terbaru per player ($70\text{ juta record}$):
* $70,000,000 \times 140\text{ bytes} \approx \mathbf{9.8\text{ GB}}$.

### Skenario 2: Append-Only Audit Log (Setiap Submission Disimpan)
Menyimpan riwayat submit harian untuk analitik:

| Metric | Calculation | Total |
| :--- | :--- | :--- |
| **Single Record (dengan Index)** | Rata-rata row `leaderboard` | **140 bytes** |
| **Storage per Hari** | $50,000,000\text{ submissions} \times 140\text{ bytes}$ | **~7.0 GB / hari** |
| **Storage per Tahun** | $7.0\text{ GB} \times 365\text{ hari}$ | **~2.55 TB / tahun** |
| **Storage untuk 5 Tahun** | $2.55\text{ TB} \times 5$ | **~12.75 TB** |

Strategi table partitioning berbasis bulan (`created_at`) dan data archiving/purging untuk data lebih dari 90 hari.

---

## 5. Network Bandwidth (Ingress & Egress)

### Ingress (Request Masuk ke Server)
* Request submit score: payload JSON `{"playerId": "...", "score": 100}` + HTTP header $\approx 300\text{ bytes}$.
* Formula: $\text{Write QPS} \times \text{Request Size}$.
* **Rata-rata Ingress**: $600\text{ QPS} \times 300\text{ bytes} = \mathbf{180\text{ KB/sec}}$ (~$1.44\text{ Mbps}$).
* **Peak Ingress**: $1,200\text{ QPS} \times 300\text{ bytes} = \mathbf{360\text{ KB/sec}}$ (~$2.88\text{ Mbps}$).

### Egress (Response Keluar dari Server)
* Response query leaderboard: return Top-10 atau Top-50 rank.
* Payload JSON Top-10 items $\approx 1.2\text{ KB}$.
* Formula: $\text{Read QPS} \times \text{Response Size}$.
* **Rata-rata Egress**: $3,000\text{ QPS} \times 1.2\text{ KB} = \mathbf{3.6\text{ MB/sec}}$ (~$28.8\text{ Mbps}$).
* **Peak Egress**: $6,000\text{ QPS} \times 1.2\text{ KB} = \mathbf{7.2\text{ MB/sec}}$ (~$57.6\text{ Mbps}$).

---

## 6. Capacity Planning Summary

| Parameter | Baseline Value|
| :--- | :--- |
| **Write QPS (Avg / Peak)** | 600 / 1,200 |
| **Read QPS (Avg / Peak)** | 3,000 / 6,000 |
| **Memory (Redis)** | ~8.96 GB | 
| **Storage (PostgreSQL - 5 Years Log)**| ~12.75 TB |
| **Storage (PostgreSQL - Snapshot Only)**| ~9.8 GB |
| **Network Ingress (Peak)** | 360 KB/sec |
| **Network Egress (Peak)** | 7.2 MB/sec |
