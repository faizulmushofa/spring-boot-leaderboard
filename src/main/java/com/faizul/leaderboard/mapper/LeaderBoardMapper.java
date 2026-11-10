package com.faizul.leaderboard.mapper;

import com.faizul.leaderboard.dtos.model.leaderboard.LeaderboardResponse;
import com.faizul.leaderboard.dtos.model.leaderboard.LeaderboardScoreRequest;
import com.faizul.leaderboard.models.Leaderboard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LeaderBoardMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Leaderboard toEntity(LeaderboardScoreRequest leaderboardScoreRequest);

    LeaderboardResponse toResponse(Leaderboard leaderboard);
}