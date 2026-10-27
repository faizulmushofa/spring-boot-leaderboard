package com.faizul.leaderboard.services.redis;

import com.faizul.leaderboard.dtos.redis.player.PlayerCache;
import com.faizul.leaderboard.mapper.PlayerMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@AllArgsConstructor
public class PlayerCacheService {
    private final PlayerMapper playerMapper;
    private ReactiveRedisTemplate<String,String> redisTemplate;

    private final String PREFIX = "player:";

    public Mono<Void> save(String playerId,PlayerCache player){
        String playerKey = PREFIX +playerId;

        Map<String,String> playerMap = Map.of(
                "name",player.name(),
                "profileImage",player.profileImage() != null ? player.profileImage() : "",
                "location",player.location() != null ? player.location() : "",
                "updateAt",player.updateAt()
        );

        return redisTemplate.opsForHash()
                .putAll(playerKey,playerMap)
                .then();
    }

    public Mono<PlayerCache> findById(String playerId) {
        String playerKey = PREFIX + playerId;

        return redisTemplate.<String, String>opsForHash()
                .entries(playerKey)
                .collectMap(e -> (String) e.getKey(), e -> (String) e.getValue())
                .flatMap(map -> {
                    if (map.isEmpty()) return Mono.empty();
                    return Mono.just(new PlayerCache(
                            map.get("name"),
                            map.get("profileImage"),
                            map.get("location"),
                            map.get("updateAt")
                    ));
                });
    }

}
