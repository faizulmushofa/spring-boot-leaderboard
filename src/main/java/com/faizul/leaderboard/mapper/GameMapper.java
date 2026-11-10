package com.faizul.leaderboard.mapper;

import com.faizul.leaderboard.dtos.model.game.GameCreateRequest;
import com.faizul.leaderboard.dtos.model.game.GameResponse;
import com.faizul.leaderboard.models.Game;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.sql.Timestamp;
import java.time.Instant;

@Mapper(componentModel = "spring")
public interface GameMapper {

    GameResponse toResponse(Game game);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Game toEntity(GameCreateRequest gameCreateRequest);

    default Instant map(Timestamp timestamp) {
        return timestamp != null ? timestamp.toInstant() : null;
    }
}