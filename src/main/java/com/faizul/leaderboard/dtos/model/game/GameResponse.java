package com.faizul.leaderboard.dtos.model.game;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public record GameResponse(
        UUID id,

        @NotBlank(message = "Nama tidak Boleh Kosong")
        @Size(max = 30 ,message = "Nama Maksimal 30 Karakter")
        String name,

        @NotNull(message = "Waktu pembuatan tidak boleh kosong")
        @PastOrPresent(message = "Waktu pembuatan tidak boleh di masa depan")
        Instant createdAt
) {
}
