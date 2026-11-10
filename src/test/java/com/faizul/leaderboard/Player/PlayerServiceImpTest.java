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

    // Skenario: User mendaftarkan player baru dengan email yang belum pernah terdaftar.
    // Ekspektasi: Pengecekan email lolos (exists == false), entity disimpan ke database PostgreSQL, dan mengembalikan PlayerResponse.
    @Test
    @DisplayName("save: berhasil menyimpan player ketika email belum digunakan")
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

    // Skenario: User mencoba mendaftar dengan email yang sudah ada di database.
    // Ekspektasi: Pengecekan email mendeteksi duplikat (exists == true), penyimpanan dibatalkan, dan melempar IllegalArgumentException ("Email Sudah di gunakan").
    @Test
    @DisplayName("save: gagal menyimpan player ketika email sudah digunakan")
    void savePlayerWhenEmailIsUnavailable() {
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
                .expectErrorMatches(err ->
                        err instanceof IllegalArgumentException && err.getMessage().equals("Email Sudah di gunakan"))
                .verify();

        verify(playerRepository, never()).save(any());
    }

    // Skenario: Pemanggilan method save dengan body null.
    // Ekspektasi: Validasi awal gagal dan melempar IllegalArgumentException ("Data request kosong").
    @Test
    @DisplayName("save: gagal ketika request bernilai null")
    void savePlayerNullRequest() {
        StepVerifier.create(playerServiceImp.save(null))
                .expectErrorMatches(err ->
                        err instanceof IllegalArgumentException && err.getMessage().equals("Data request kosong"))
                .verify();
    }
}