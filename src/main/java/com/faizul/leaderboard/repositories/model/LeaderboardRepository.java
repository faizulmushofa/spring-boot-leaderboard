package com.faizul.leaderboard.repositories.model;

import com.faizul.leaderboard.models.Leaderboard;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface LeaderboardRepository extends CrudRepository<Leaderboard, UUID> {
}
