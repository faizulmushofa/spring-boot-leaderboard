package com.faizul.leaderboard.services;

import com.faizul.leaderboard.dtos.player.PlayerCreateRequest;
import com.faizul.leaderboard.dtos.player.PlayerResponse;
import com.faizul.leaderboard.mapper.PlayerMapper;
import com.faizul.leaderboard.models.Player;
import com.faizul.leaderboard.repositories.PlayerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Transactional@Service
public class PlayerService {

    private PlayerMapper playerMapper;
    private PlayerRepository playerRepository;


    public Mono<PlayerResponse> save(PlayerCreateRequest request) {
        if (request == null) {
            return Mono.error(
                    new IllegalArgumentException("Data request kosong")
            );
        }

        return playerRepository.existsPlayerByEmail(request.email())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Email Sudah di gunakan"));
                    }

                    return playerRepository.save(playerMapper.toEntity(request))
                            .map(playerMapper::toResponse);
                });
    }
}
