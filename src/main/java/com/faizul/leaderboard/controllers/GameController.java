package com.faizul.leaderboard.controllers;

import com.faizul.leaderboard.dtos.model.game.GameCreateRequest;
import com.faizul.leaderboard.dtos.model.game.GameResponse;
import com.faizul.leaderboard.services.model.GameService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping
    public Mono<ResponseEntity<GameResponse>> create(@Valid @RequestBody GameCreateRequest request){

        return gameService
                .save(request)
                .map(response -> ResponseEntity.status(201)
                        .body(response));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<GameResponse>> getGame(@PathVariable String id){
        return gameService.findById(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Flux<GameResponse> getAllGame(){
        return gameService.findAll();
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<String>> deleteGame(@PathVariable String id){
        return gameService.delete(id)
                .map(ResponseEntity::ok);
    }

}
