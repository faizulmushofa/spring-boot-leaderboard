package com.faizul.leaderboard.repositories;

import com.faizul.leaderboard.models.Leaderboard;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface LeaderboardRepository extends CrudRepository<Leaderboard, UUID> {
}
