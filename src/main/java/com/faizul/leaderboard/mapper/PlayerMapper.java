package com.faizul.leaderboard.mapper;

import com.faizul.leaderboard.dtos.model.player.PlayerCreateRequest;
import com.faizul.leaderboard.dtos.model.player.PlayerResponse;
import com.faizul.leaderboard.models.Player;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.sql.Timestamp;
import java.time.Instant;

@Mapper(componentModel = "spring")
public interface PlayerMapper {

    PlayerResponse toResponse(Player player);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    Player toEntity(PlayerCreateRequest playerCreateRequest);

    default Instant map(Timestamp timestamp) {
        return timestamp != null ? timestamp.toInstant() : null;
    }
}