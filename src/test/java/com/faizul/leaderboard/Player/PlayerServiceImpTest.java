package com.faizul.leaderboard.Player;

import com.faizul.leaderboard.dtos.model.player.PlayerCreateRequest;
import com.faizul.leaderboard.dtos.model.player.PlayerResponse;
import com.faizul.leaderboard.mapper.PlayerMapper;
import com.faizul.leaderboard.models.Player;
import com.faizul.leaderboard.repositories.model.PlayerRepository;
import com.faizul.leaderboard.services.model.PlayerServiceImp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class PlayerServiceImpTest {

    @Mock
    PlayerRepository playerRepository;
    @Mock
    PlayerMapper playerMapper;
    @InjectMocks
    PlayerServiceImp playerServiceImp;

    @Test
    @DisplayName("Test Berhasil : Berhasil Menyimpan player ketika Email tersedia")
    void savesPlayerWhenEmailIsAvailable() {
        var gameId = UUID.randomUUID();
        var request = new PlayerCreateRequest(
                "user1",
                "user1@test.com",
                null,
                "Jakarta",
                gameId
        );

        var player = Player.builder().email(request.email()).build();
        var response = new PlayerResponse(
                UUID.randomUUID(), "Faizul", null, "Jakarta",
                Instant.now(), Instant.now(), gameId
        );

        when(playerRepository.existsPlayerByEmail(request.email()))
                .thenReturn(Mono.just(false));

        when(playerMapper.toEntity(request)).thenReturn(player);

        when(playerRepository.save(player)).thenReturn(Mono.just(player));

        when(playerMapper.toResponse(player)).thenReturn(response);


        var result = playerServiceImp.save(request);


        StepVerifier.create(result)
                .expectNext(response)
                .verifyComplete();


        verify(playerRepository).save(player);
    }


    @Test
    @DisplayName("gagal Menyimpan player ketika Email tidak tersedia")
    void savePlayerWhenEmailIsUnavailable(){
        var gameId = UUID.randomUUID();
        var request = new PlayerCreateRequest(
                "user1",
                "user1@test.com",
                null,
                "Jakarta",
                gameId
        );

        when(playerRepository.existsPlayerByEmail(request.email()))
                .thenReturn(Mono.just(true));

        StepVerifier.create(playerServiceImp.save(request))
                .expectErrorMatches( err ->
                        err instanceof  IllegalArgumentException && err.getMessage().equals("Email Sudah di gunakan"))
                .verify();

//        verify(playerRepository,never()).save(any());
    }

    //

}
