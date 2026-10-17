package com.faizul.leaderboard.repositories;

import com.faizul.leaderboard.models.Game;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface GameRepository extends ReactiveCrudRepository<Game, UUID> {
}
