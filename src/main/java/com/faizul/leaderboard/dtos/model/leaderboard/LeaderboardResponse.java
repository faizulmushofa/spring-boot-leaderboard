package com.faizul.leaderboard.dtos.model.leaderboard;

import java.sql.Timestamp;
import java.util.UUID;

public record LeaderboardResponse(
        UUID id,
        UUID gameId,
        UUID playerId,
        Integer score,
        Timestamp createdAt
) {
}