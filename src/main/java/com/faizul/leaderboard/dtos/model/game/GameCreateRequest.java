package com.faizul.leaderboard.dtos.model.game;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record GameCreateRequest(

        @NotBlank(message = "Nama Game Tidak Boleh Kosong")
        @Size(max = 64,message = "Maksimal nama game 64 karakter")
        String name
) {
}
