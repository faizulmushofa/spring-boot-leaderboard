package com.faizul.leaderboard.repositories.model;

import com.faizul.leaderboard.models.Leaderboard;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface LeaderboardRepository extends ReactiveCrudRepository<Leaderboard, UUID> {

    @Query("SELECT * FROM leaderboard WHERE game_id = :gameId AND player_id = :playerId LIMIT 1")
    Mono<Leaderboard> findByGameIdAndPlayerId(UUID gameId, UUID playerId);

    @Query("SELECT * FROM leaderboard WHERE game_id = :gameId ORDER BY score DESC LIMIT :limit")
    Flux<Leaderboard> findTopByGameIdOrderByScoreDesc(UUID gameId, Long limit);
}
