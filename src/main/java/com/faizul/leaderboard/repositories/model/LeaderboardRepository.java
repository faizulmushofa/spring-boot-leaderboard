package com.faizul.leaderboard.repositories.model;

import com.faizul.leaderboard.models.Leaderboard;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface LeaderboardRepository extends ReactiveCrudRepository<Leaderboard, UUID> {
}
