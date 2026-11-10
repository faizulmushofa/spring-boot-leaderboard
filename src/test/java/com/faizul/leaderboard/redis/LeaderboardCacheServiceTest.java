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
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveZSetOperations;
import org.springframework.data.redis.core.ZSetOperations;
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

    // Skenario: Menambahkan atau mengupdate skor pemain ke dalam Redis Sorted Set (ZSet).
    // Ekspektasi: Redis ZSet menambahkan key "leaderboard:{gameId}" dengan member playerId dan score, mengembalikan Mono<Boolean> true.
    @Test
    @DisplayName("addScore: berhasil menambah skor player ke ZSET")
    void addScore() {
        when(zSetOps.add(KEY, PLAYER_ID, 100.0)).thenReturn(Mono.just(true));

        StepVerifier.create(leaderboardCacheService.addScore(GAME_ID, PLAYER_ID, 100.0))
                .expectNext(true)
                .verifyComplete();

        verify(zSetOps).add(KEY, PLAYER_ID, 100.0);
    }

    // Skenario: Menambah (increment) skor yang sudah ada pada player sebesar delta tertentu.
    // Ekspektasi: Redis ZINCRBY dijalankan dan mengembalikan akumulasi skor terbaru (100.0 + 50.0 = 150.0).
    @Test
    @DisplayName("incrementScore: berhasil menambahkan nilai skor player")
    void incrementScore() {
        when(zSetOps.incrementScore(KEY, PLAYER_ID, 50.0)).thenReturn(Mono.just(150.0));

        StepVerifier.create(leaderboardCacheService.incrementScore(GAME_ID, PLAYER_ID, 50.0))
                .expectNext(150.0)
                .verifyComplete();

        verify(zSetOps).incrementScore(KEY, PLAYER_ID, 50.0);
    }

    // Skenario (Cache Hit): Membaca nilai skor pemain yang tercatat di Redis ZSet.
    // Ekspektasi: Redis ZSCORE mengembalikan Double nilai skor pemain.
    @Test
    @DisplayName("getScore: player ada — kembalikan skor")
    void getScoreHit() {
        when(zSetOps.score(KEY, PLAYER_ID)).thenReturn(Mono.just(200.0));

        StepVerifier.create(leaderboardCacheService.getScore(GAME_ID, PLAYER_ID))
                .expectNext(200.0)
                .verifyComplete();

        verify(zSetOps).score(KEY, PLAYER_ID);
    }

    // Skenario (Cache Miss): Membaca skor pemain yang belum pernah terdaftar di Redis ZSet.
    // Ekspektasi: Redis ZSCORE mengembalikan Mono.empty() tanpa error.
    @Test
    @DisplayName("getScore: player tidak ada — kembalikan empty")
    void getScoreMiss() {
        when(zSetOps.score(KEY, PLAYER_ID)).thenReturn(Mono.empty());

        StepVerifier.create(leaderboardCacheService.getScore(GAME_ID, PLAYER_ID))
                .verifyComplete();

        verify(zSetOps).score(KEY, PLAYER_ID);
    }

    // Skenario: Mengambil urutan peringkat player berdasarkan skor tertinggi ke terendah (ZREVRANK).
    // Ekspektasi: Mengembalikan indeks posisi 0-based pemain (misal: 0L untuk peringkat ke-1).
    @Test
    @DisplayName("getRank: kembalikan peringkat 0-based")
    void getRank() {
        when(zSetOps.reverseRank(KEY, PLAYER_ID)).thenReturn(Mono.just(0L));

        StepVerifier.create(leaderboardCacheService.getRank(GAME_ID, PLAYER_ID))
                .expectNext(0L)
                .verifyComplete();

        verify(zSetOps).reverseRank(KEY, PLAYER_ID);
    }

    // Skenario: Mengambil daftar playerId urutan 1 s/d count tertinggi (tanpa skor).
    // Ekspektasi: Redis ZREVRANGE mengembalikan stream daftar playerId sesuai rentang Range.closed(0, count-1).
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

    // Skenario: Mengambil daftar pemain teratas lengkap dengan nilai skornya masing-masing.
    // Ekspektasi: Redis ZREVRANGE ... WITHSCORES mengembalikan tuple berisi (playerId, score) terurut skor tertinggi.
    @Test
    @DisplayName("getTopWithScore: kembalikan daftar tuple playerId beserta skornya")
    void getTopWithScore() {
        ZSetOperations.TypedTuple<String> tuple1 = new DefaultTypedTuple<>("p1", 500.0);
        ZSetOperations.TypedTuple<String> tuple2 = new DefaultTypedTuple<>("p2", 400.0);

        when(zSetOps.reverseRangeWithScores(KEY, Range.closed(0L, 1L)))
                .thenReturn(Flux.just(tuple1, tuple2));

        StepVerifier.create(leaderboardCacheService.getTopWithScore(GAME_ID, 2L))
                .expectNext(tuple1, tuple2)
                .verifyComplete();

        verify(zSetOps).reverseRangeWithScores(KEY, Range.closed(0L, 1L));
    }

    // Skenario: Menghapus player dari Sorted Set leaderboard (ZREM).
    // Ekspektasi: Member playerId dihapus dari key ZSet, mengembalikan jumlah baris yang terhapus (1L).
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