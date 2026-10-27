package com.faizul.leaderboard.services.model;

import com.faizul.leaderboard.dtos.model.player.PlayerCreateRequest;
import com.faizul.leaderboard.dtos.model.player.PlayerResponse;
import com.faizul.leaderboard.mapper.PlayerMapper;
import com.faizul.leaderboard.repositories.model.PlayerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Transactional
@Service
public class PlayerServiceImp {

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

    public Mono<PlayerResponse> findById(String playerId) {
        // ponytail: LB-107 — cache-first Redis → fallback PostgreSQL, implementasi setelah LB-104 selesai
        return Mono.error(new UnsupportedOperationException("LB-107: belum diimplementasi"));
    }
}
