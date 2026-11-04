package com.faizul.leaderboard.repositories.model;

import com.faizul.leaderboard.models.Game;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface GameRepository extends ReactiveCrudRepository<Game, UUID> {

    Mono<Boolean> existsGameByName(String name);
}
