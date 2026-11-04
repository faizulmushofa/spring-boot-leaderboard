package com.faizul.leaderboard.services.model;

import com.faizul.leaderboard.dtos.model.game.GameCreateRequest;
import com.faizul.leaderboard.dtos.model.game.GameResponse;
import com.faizul.leaderboard.mapper.GameMapper;
import com.faizul.leaderboard.repositories.model.GameRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final GameMapper gameMapper;


    public Mono<GameResponse> save(GameCreateRequest request){

        if (request == null){
            return Mono.error(
                    new IllegalArgumentException("Data Request Kosong")
            );
        }

        return gameRepository.existsGameByName(request.name())
                .flatMap(exist -> {
                    if (exist){
                        return Mono.error(new IllegalArgumentException("Nama Game sudah ada"));
                    }

                    Game game = gameMapper.toEntity(request);
                    game.setId(UUID.randomUUID());
                    game.setCreated_at(Timestamp.from(Instant.now()));
                    return gameRepository.save(game)
                            .map(gameMapper::toResponse);
                });

    }

    public Mono<GameResponse> findById(String uuid){

        if (uuid == null){
            return Mono.error(
                    new IllegalArgumentException("Uuid kosong")
            );
        }

        return gameRepository.findById(UUID.fromString(uuid))
                .switchIfEmpty(
                        Mono.error(
                                new IllegalArgumentException("Id Game tidak di temukan")
                        )
                ).map(
                     gameMapper::toResponse
                );

    }

    public Flux<GameResponse> findAll(){
        return gameRepository.findAll()
                .filter(game -> game.getDeleted_at() == null)
                .map(gameMapper::toResponse);
    }

    public Mono<String> delete(String uuid){
        if (uuid == null){
            return Mono
                    .error(
                            new IllegalArgumentException("Request kosong")
                    );
        }

        return gameRepository.findById(UUID.fromString(uuid))
                .switchIfEmpty(Mono.error(
                        new IllegalArgumentException("Game tidak ditemukan")
                ))
                .flatMap(game -> {
                    game.setDeleted_at(Timestamp.from(Instant.now()));
                    return gameRepository.save(game);
                })
                .map(game -> "Game berhasil dihapus");

    }

}
