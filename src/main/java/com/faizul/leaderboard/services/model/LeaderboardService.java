package com.faizul.leaderboard.services.model;

import com.faizul.leaderboard.dtos.model.leaderboard.LeaderboardEntryResponse;
import com.faizul.leaderboard.dtos.model.leaderboard.LeaderboardResponse;
import com.faizul.leaderboard.dtos.model.leaderboard.LeaderboardScoreRequest;
import com.faizul.leaderboard.mapper.LeaderBoardMapper;
import com.faizul.leaderboard.models.Leaderboard;
import com.faizul.leaderboard.repositories.model.LeaderboardRepository;
import com.faizul.leaderboard.services.redis.LeaderboardCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final LeaderboardRepository leaderboardRepository;
    private final LeaderboardCacheService leaderboardCacheService;
    private final PlayerServiceImp playerService;
    private final LeaderBoardMapper leaderboardMapper;

    @Transactional
    public Mono<LeaderboardResponse> recordScore(LeaderboardScoreRequest request) {
        if (request == null) {
            return Mono.error(new IllegalArgumentException("Request tidak boleh kosong"));
        }

        return leaderboardRepository.findByGameIdAndPlayerId(request.gameId(), request.playerId())
                .flatMap(existing -> {
                    if (request.score() > existing.getScore()) {
                        existing.setScore(request.score());
                        existing.setCreatedAt(Timestamp.from(Instant.now()));
                        return leaderboardRepository.save(existing);
                    }
                    return Mono.just(existing);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    Leaderboard newEntry = leaderboardMapper.toEntity(request);
                    newEntry.setId(UUID.randomUUID());
                    newEntry.setCreatedAt(Timestamp.from(Instant.now()));
                    return leaderboardRepository.save(newEntry);
                }))
                .flatMap(saved ->
                        leaderboardCacheService.addScore(
                                saved.getGameId().toString(),
                                saved.getPlayerId().toString(),
                                saved.getScore().doubleValue()
                        ).thenReturn(leaderboardMapper.toResponse(saved))
                );
    }

    public Flux<LeaderboardEntryResponse> getTopLeaderboard(UUID gameId, Long count) {
        AtomicLong rankCounter = new AtomicLong(1);

        return leaderboardCacheService.getTopWithScore(gameId.toString(), count)
                .flatMapSequential(tuple -> {
                    String playerId = tuple.getValue();
                    Double score = tuple.getScore();
                    long rank = rankCounter.getAndIncrement();

                    return playerService.findById(playerId)
                            .map(player -> new LeaderboardEntryResponse(
                                    rank,
                                    UUID.fromString(playerId),
                                    player.name(),
                                    player.profileImage(),
                                    score
                            ));
                })
                .switchIfEmpty(Flux.defer(() -> warmupAndGetFromDb(gameId, count)));
    }

    public Mono<LeaderboardEntryResponse> getPlayerRank(UUID gameId, UUID playerId) {
        String gameIdStr = gameId.toString();
        String playerIdStr = playerId.toString();

        return Mono.zip(
                leaderboardCacheService.getRank(gameIdStr, playerIdStr).map(r -> r + 1),
                leaderboardCacheService.getScore(gameIdStr, playerIdStr),
                playerService.findById(playerIdStr)
        ).map(tuple -> new LeaderboardEntryResponse(
                tuple.getT1(),
                playerId,
                tuple.getT3().name(),
                tuple.getT3().profileImage(),
                tuple.getT2()
        ));
    }

    private Flux<LeaderboardEntryResponse> warmupAndGetFromDb(UUID gameId, Long count) {
        AtomicLong rankCounter = new AtomicLong(1);

        return leaderboardRepository.findTopByGameIdOrderByScoreDesc(gameId, count)
                .concatMap(lb ->
                        leaderboardCacheService.addScore(
                                lb.getGameId().toString(),
                                lb.getPlayerId().toString(),
                                lb.getScore().doubleValue()
                        ).then(playerService.findById(lb.getPlayerId().toString()))
                         .map(player -> new LeaderboardEntryResponse(
                                 rankCounter.getAndIncrement(),
                                 lb.getPlayerId(),
                                 player.name(),
                                 player.profileImage(),
                                 lb.getScore().doubleValue()
                         ))
                );
    }
}