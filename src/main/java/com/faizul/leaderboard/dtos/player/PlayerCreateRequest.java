package com.faizul.leaderboard.dtos.player;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record PlayerCreateRequest(
        @NotBlank(message = "Nama tidak Boleh Kosong")
        @Size(max = 30 ,message = "Nama Maksimal 30 Karakter")
        String name,

        @NotBlank(message = "Email tidak boleh kosong")
        @Email(message = "Format email tidak valid")
        @Size(max = 254, message = "Email maksimal 254 karakter")
        String email,


        @Size(max = 2048,message = "Url Gambar terlalu panjang")
        String profileImage,

        @Size(max = 255, message = "Lokasi maksimal 255 karakter")
        String location,

        @NotNull(
                message = "Data ID game tidak boleh Kosong"
        )
        UUID gameId
) {
}
