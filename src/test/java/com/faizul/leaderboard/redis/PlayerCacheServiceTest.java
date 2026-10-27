package com.faizul.leaderboard.redis;

import com.faizul.leaderboard.dtos.redis.player.PlayerCache;
import com.faizul.leaderboard.services.redis.PlayerCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlayerCacheServiceTest {

    @Mock
    ReactiveRedisTemplate<String, String> redisTemplate;

    @Mock
    ReactiveHashOperations<String, String, String> hashOps;

    @InjectMocks
    PlayerCacheService playerCacheService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.<String, String>opsForHash()).thenReturn(hashOps);
    }

    @Test
    @DisplayName("save: berhasil menyimpan player cache ke Redis")
    void savePlayerCache() {
        var playerId = "uuid-123";
        var cache = new PlayerCache("Rick", "s3://img.jpg", "Jakarta", "2026-01-01T00:00:00Z");

        when(hashOps.putAll(anyString(), anyMap())).thenReturn(Mono.just(true));

        StepVerifier.create(playerCacheService.save(playerId, cache))
                .verifyComplete();

        verify(hashOps).putAll(anyString(), anyMap());
    }

    @Test
    @DisplayName("save: profileImage null tidak throw NPE")
    void savePlayerCacheWithNullProfileImage() {
        var playerId = "uuid-123";
        var cache = new PlayerCache("Rick", null, "Jakarta", "2026-01-01T00:00:00Z");

        when(hashOps.putAll(anyString(), anyMap())).thenReturn(Mono.just(true));

        StepVerifier.create(playerCacheService.save(playerId, cache))
                .verifyComplete();
    }

    @Test
    @DisplayName("findById: cache hit — kembalikan PlayerCache")
    void findByIdCacheHit() {
        var playerId = "uuid-123";

        when(hashOps.entries("player:" + playerId)).thenReturn(Flux.just(
                Map.entry("name", "Rick"),
                Map.entry("profileImage", "s3://img.jpg"),
                Map.entry("location", "Jakarta"),
                Map.entry("updateAt", "2026-01-01T00:00:00Z")
        ));

        StepVerifier.create(playerCacheService.findById(playerId))
                .expectNextMatches(cache ->
                        cache.name().equals("Rick") &&
                        cache.location().equals("Jakarta") &&
                        cache.profileImage().equals("s3://img.jpg")
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("findById: cache miss — kembalikan empty")
    void findByIdCacheMiss() {
        var playerId = "uuid-999";

        when(hashOps.entries("player:" + playerId)).thenReturn(Flux.empty());

        StepVerifier.create(playerCacheService.findById(playerId))
                .verifyComplete();
    }
}
