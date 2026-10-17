package com.faizul.leaderboard.repositories;

import com.faizul.leaderboard.models.Friendship;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface FriendshipRepository extends ReactiveCrudRepository<Friendship,UUID> {
}
