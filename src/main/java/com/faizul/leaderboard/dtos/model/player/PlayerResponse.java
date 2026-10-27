package com.faizul.leaderboard.dtos.model.player;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public record PlayerResponse(

        @NotNull(message = "ID player tidak boleh kosong")
        UUID id,

        @NotBlank(message = "Nama player tidak boleh kosong")
        @Size(max = 100, message = "Nama maksimal 100 karakter")
        String name,

        @Size(max = 2048, message = "URL gambar maksimal 2048 karakter")
        String profileImage,

        @Size(max = 255, message = "Lokasi maksimal 255 karakter")
        String location,

        @NotNull(message = "Waktu pembuatan tidak boleh kosong")
        @PastOrPresent(message = "Waktu pembuatan tidak boleh di masa depan")
        Instant createdAt,

        @PastOrPresent(message = "Waktu login terakhir tidak boleh di masa depan")
        Instant lastLogin,

        @NotNull(message = "ID game tidak boleh kosong")
        UUID gameId

) {
}