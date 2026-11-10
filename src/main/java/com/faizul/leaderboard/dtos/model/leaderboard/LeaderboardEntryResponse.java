package com.faizul.leaderboard.dtos.model.leaderboard;

import java.util.UUID;

public record LeaderboardEntryResponse(
        Long rank,
        UUID playerId,
        String playerName,
        String profileImage,
        Double score
) {
}