package com.faizul.leaderboard.repositories;

import com.faizul.leaderboard.models.Player;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PlayerRepository extends CrudRepository<Player, UUID> {
}
