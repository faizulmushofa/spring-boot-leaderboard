package com.faizul.leaderboard.mapper;

import com.faizul.leaderboard.dtos.model.game.GameCreateRequest;
import com.faizul.leaderboard.dtos.model.game.GameResponse;
import com.faizul.leaderboard.models.Game;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GameMapper {
    GameResponse toResponse(Game game);
    Game toEntity(GameCreateRequest gameCreateRequest);

}
