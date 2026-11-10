package com.faizul.leaderboard.dtos.model.leaderboard;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record LeaderboardScoreRequest(
        @NotNull(message = "Id game tidak boleh kosong")
        UUID gameId,

        @NotNull(message = "Player Id tidak boleh kosong")
        UUID playerId,

        @NotNull(message = "Score tidak boleh kosong")
        @PositiveOrZero(message = "Score tidak boleh negatif")
        Integer score
) {
}
