package com.faizul.leaderboard.redis;

import com.faizul.leaderboard.services.redis.LeaderboardCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveZSetOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LeaderboardCacheServiceTest {

    @Mock
    ReactiveRedisTemplate<String, String> redisTemplate;

    @Mock
    ReactiveZSetOperations<String, String> zSetOps;

    @InjectMocks
    LeaderboardCacheService leaderboardCacheService;

    private final String GAME_ID = "game-1";
    private final String PLAYER_ID = "player-1";
    private final String KEY = "leaderboard:" + GAME_ID;

    @BeforeEach
    void setUp() {
        when(redisTemplate.<String, String>opsForZSet()).thenReturn(zSetOps);
    }

    @Test
    @DisplayName("addScore: berhasil menambah skor player ke ZSET")
    void addScore() {
        when(zSetOps.add(KEY, PLAYER_ID, 100.0)).thenReturn(Mono.just(true));

        StepVerifier.create(leaderboardCacheService.addScore(GAME_ID, PLAYER_ID, 100.0))
                .expectNext(true)
                .verifyComplete();

        verify(zSetOps).add(KEY, PLAYER_ID, 100.0);
    }

    @Test
    @DisplayName("incrementScore: berhasil menambahkan nilai skor player")
    void incrementScore() {
        when(zSetOps.incrementScore(KEY, PLAYER_ID, 50.0)).thenReturn(Mono.just(150.0));

        StepVerifier.create(leaderboardCacheService.incrementScore(GAME_ID, PLAYER_ID, 50.0))
                .expectNext(150.0)
                .verifyComplete();

        verify(zSetOps).incrementScore(KEY, PLAYER_ID, 50.0);
    }

    @Test
    @DisplayName("getScore: player ada — kembalikan skor")
    void getScoreHit() {
        when(zSetOps.score(KEY, PLAYER_ID)).thenReturn(Mono.just(200.0));

        StepVerifier.create(leaderboardCacheService.getScore(GAME_ID, PLAYER_ID))
                .expectNext(200.0)
                .verifyComplete();

        verify(zSetOps).score(KEY, PLAYER_ID);
    }

    @Test
    @DisplayName("getScore: player tidak ada — kembalikan empty")
    void getScoreMiss() {
        when(zSetOps.score(KEY, PLAYER_ID)).thenReturn(Mono.empty());

        StepVerifier.create(leaderboardCacheService.getScore(GAME_ID, PLAYER_ID))
                .verifyComplete();

        verify(zSetOps).score(KEY, PLAYER_ID);
    }

    @Test
    @DisplayName("getRank: kembalikan peringkat 0-based")
    void getRank() {
        when(zSetOps.reverseRank(KEY, PLAYER_ID)).thenReturn(Mono.just(0L));

        StepVerifier.create(leaderboardCacheService.getRank(GAME_ID, PLAYER_ID))
                .expectNext(0L)
                .verifyComplete();

        verify(zSetOps).reverseRank(KEY, PLAYER_ID);
    }

    @Test
    @DisplayName("getTop: kembalikan daftar playerId teratas")
    void getTop() {
        when(zSetOps.reverseRange(KEY, Range.closed(0L, 2L)))
                .thenReturn(Flux.just("p1", "p2", "p3"));

        StepVerifier.create(leaderboardCacheService.getTop(GAME_ID, 3L))
                .expectNext("p1", "p2", "p3")
                .verifyComplete();

        verify(zSetOps).reverseRange(KEY, Range.closed(0L, 2L));
    }

    @Test
    @DisplayName("removePlayer: berhasil menghapus player dari leaderboard")
    void removePlayer() {
        when(zSetOps.remove(KEY, PLAYER_ID)).thenReturn(Mono.just(1L));

        StepVerifier.create(leaderboardCacheService.removePlayer(GAME_ID, PLAYER_ID))
                .expectNext(1L)
                .verifyComplete();

        verify(zSetOps).remove(KEY, PLAYER_ID);
    }
}
