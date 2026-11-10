package com.faizul.leaderboard.controllers;

import com.faizul.leaderboard.dtos.model.leaderboard.LeaderboardEntryResponse;
import com.faizul.leaderboard.dtos.model.leaderboard.LeaderboardResponse;
import com.faizul.leaderboard.dtos.model.leaderboard.LeaderboardScoreRequest;
import com.faizul.leaderboard.services.model.LeaderboardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/leaderboards")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @PostMapping("/scores")
    public Mono<ResponseEntity<LeaderboardResponse>> recordScore(
            @Valid @RequestBody LeaderboardScoreRequest request
    ) {
        return leaderboardService.recordScore(request)
                .map(response -> ResponseEntity.status(201).body(response));
    }

    @GetMapping("/{gameId}/top")
    public Flux<LeaderboardEntryResponse> getTopLeaderboard(
            @PathVariable UUID gameId,
            @RequestParam(defaultValue = "10") Long count
    ) {
        return leaderboardService.getTopLeaderboard(gameId, count);
    }

    @GetMapping("/{gameId}/players/{playerId}/rank")
    public Mono<ResponseEntity<LeaderboardEntryResponse>> getPlayerRank(
            @PathVariable UUID gameId,
            @PathVariable UUID playerId
    ) {
        return leaderboardService.getPlayerRank(gameId, playerId)
                .map(ResponseEntity::ok);
    }
}