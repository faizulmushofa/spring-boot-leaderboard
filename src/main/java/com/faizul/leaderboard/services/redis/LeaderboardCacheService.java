package com.faizul.leaderboard.services.redis;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
@AllArgsConstructor
public class LeaderboardCacheService {

    private final ReactiveRedisTemplate<String,String> redisTemplate;
    private final String PREFIX = "leaderboard:";

    public Mono<Boolean> addScore(String gameId,String playerId,double score){

        String leaderboardKey = PREFIX + gameId;

        return redisTemplate.opsForZSet()
                .add(leaderboardKey,playerId,score);
    }

    public Mono<Double> incrementScore(String gameId,String playerId,double delta){

        String leaderboardKey = PREFIX + gameId;

        return redisTemplate.opsForZSet()
                .incrementScore(leaderboardKey,playerId,delta);

    }

    public Mono<Double> getScore (String gameId,String playerId){
        String leaderboardKey = PREFIX + gameId;

        return redisTemplate.opsForZSet()
                .score(leaderboardKey,playerId);

    }

    public Mono<Long> getRank (String gameId,String playerId){

        String leaderboardKey = PREFIX + gameId;

        return redisTemplate.opsForZSet()
                .reverseRank(leaderboardKey,playerId);


    }

    public Flux<String> getTop (String gameId,Long count){

        String leaderboardKey = PREFIX + gameId;

        return  redisTemplate.opsForZSet()
                .reverseRange(leaderboardKey, Range.closed(0L,count -1));
    }

    public Mono<Long> removePlayer (String gameId,String playerId){

        String leaderboardKey = PREFIX + gameId;

        return  redisTemplate.opsForZSet()
                .remove(leaderboardKey,playerId);

    }


}
