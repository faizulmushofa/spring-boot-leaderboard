package com.faizul.leaderboard.controllers;

import com.faizul.leaderboard.dtos.player.PlayerCreateRequest;
import com.faizul.leaderboard.dtos.player.PlayerResponse;
import com.faizul.leaderboard.services.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping
    public Mono<ResponseEntity<PlayerResponse>> create(
            @Valid @RequestBody PlayerCreateRequest request
    ) {
        return playerService.save(request)
                .map(response -> ResponseEntity.status(201).body(response));
    }
}
