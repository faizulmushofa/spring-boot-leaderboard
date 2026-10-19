package com.faizul.leaderboard.mapper;

import com.faizul.leaderboard.dtos.player.PlayerCreateRequest;
import com.faizul.leaderboard.dtos.player.PlayerResponse;
import com.faizul.leaderboard.models.Player;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlayerMapper {
    PlayerResponse toResponse(Player player);
    Player toEntity(PlayerCreateRequest playerCreateRequest);
}
