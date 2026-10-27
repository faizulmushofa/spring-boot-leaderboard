package com.faizul.leaderboard.dtos.redis.player;

public record PlayerCache(
        String name,
        String profileImage,
        String location,
        String updateAt
) {}
