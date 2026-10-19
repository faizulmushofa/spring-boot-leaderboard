package com.faizul.leaderboard.repositories;

import com.faizul.leaderboard.models.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface PlayerRepository extends ReactiveCrudRepository<Player, UUID> {
    Mono<Boolean> existsPlayerByEmail(String email);
}
