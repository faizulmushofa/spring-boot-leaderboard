package com.faizul.leaderboard.game;

import com.faizul.leaderboard.dtos.model.game.GameCreateRequest;
import com.faizul.leaderboard.dtos.model.game.GameResponse;
import com.faizul.leaderboard.mapper.GameMapper;
import com.faizul.leaderboard.models.Game;
import com.faizul.leaderboard.repositories.model.GameRepository;
import com.faizul.leaderboard.services.model.GameService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

    @Mock
    GameRepository gameRepository;

    @Mock
    GameMapper gameMapper;

    @InjectMocks
    GameService gameService;

    // Skenario: User membuat game dengan nama yang belum pernah dipakai.
    // Ekspektasi: Sistem memvalidasi nama via repository, menyimpan game baru dengan ID & createdAt, lalu mengembalikan GameResponse (HTTP 201).
    @Test
    @DisplayName("save: sukses menyimpan game ketika nama game belum terdaftar")
    void saveGameSuccess() {
        GameCreateRequest request = new GameCreateRequest("Mobile Legends");
        UUID generatedId = UUID.randomUUID();
        Game entity = Game.builder().name("Mobile Legends").build();
        Game savedEntity = Game.builder().id(generatedId).name("Mobile Legends").createdAt(Timestamp.from(Instant.now())).build();
        GameResponse response = new GameResponse(generatedId, "Mobile Legends", Instant.now());

        when(gameRepository.existsGameByName(request.name())).thenReturn(Mono.just(false));
        when(gameMapper.toEntity(request)).thenReturn(entity);
        when(gameRepository.save(any(Game.class))).thenReturn(Mono.just(savedEntity));
        when(gameMapper.toResponse(savedEntity)).thenReturn(response);

        StepVerifier.create(gameService.save(request))
                .expectNext(response)
                .verifyComplete();

        verify(gameRepository).existsGameByName("Mobile Legends");
        verify(gameRepository).save(any(Game.class));
    }

    // Skenario: User mencoba membuat game baru, tetapi nama game sudah ada di database.
    // Ekspektasi: Operasi dibatalkan, melempar IllegalArgumentException dengan pesan "Nama Game sudah ada".
    @Test
    @DisplayName("save: gagal ketika nama game sudah digunakan")
    void saveGameDuplicateName() {
        GameCreateRequest request = new GameCreateRequest("Mobile Legends");

        when(gameRepository.existsGameByName(request.name())).thenReturn(Mono.just(true));

        StepVerifier.create(gameService.save(request))
                .expectErrorMatches(err -> err instanceof IllegalArgumentException && err.getMessage().equals("Nama Game sudah ada"))
                .verify();

        verify(gameRepository, never()).save(any());
    }

    // Skenario: Client memanggil method save tanpa menyertakan body (request bernilai null).
    // Ekspektasi: Validasi awal gagal, melempar IllegalArgumentException ("Data Request Kosong").
    @Test
    @DisplayName("save: gagal validasi ketika request null")
    void saveGameNullRequest() {
        StepVerifier.create(gameService.save(null))
                .expectErrorMatches(err -> err instanceof IllegalArgumentException && err.getMessage().equals("Data Request Kosong"))
                .verify();
    }

    // Skenario: Mencari detail game menggunakan ID yang valid dan ada di database.
    // Ekspektasi: Mengembalikan objek GameResponse yang sesuai.
    @Test
    @DisplayName("findById: sukses mengambil data game saat UUID ditemukan")
    void findByIdSuccess() {
        UUID gameId = UUID.randomUUID();
        Game game = Game.builder().id(gameId).name("Dota 2").build();
        GameResponse response = new GameResponse(gameId, "Dota 2", Instant.now());

        when(gameRepository.findById(gameId)).thenReturn(Mono.just(game));
        when(gameMapper.toResponse(game)).thenReturn(response);

        StepVerifier.create(gameService.findById(gameId.toString()))
                .expectNext(response)
                .verifyComplete();

        verify(gameRepository).findById(gameId);
    }

    // Skenario: Mencari game dengan UUID yang tidak tercatat di database.
    // Ekspektasi: Mono kosong dari repository memicu switchIfEmpty dan melempar IllegalArgumentException ("Id Game tidak di temukan").
    @Test
    @DisplayName("findById: gagal ketika UUID tidak ada di database")
    void findByIdNotFound() {
        UUID gameId = UUID.randomUUID();

        when(gameRepository.findById(gameId)).thenReturn(Mono.empty());

        StepVerifier.create(gameService.findById(gameId.toString()))
                .expectErrorMatches(err -> err instanceof IllegalArgumentException && err.getMessage().equals("Id Game tidak di temukan"))
                .verify();
    }

    // Skenario: Mengambil daftar seluruh game yang ada.
    // Ekspektasi: Hanya game yang belum dihapus (deletedAt == null) yang disertakan, game yang sudah soft-deleted disaring keluar.
    @Test
    @DisplayName("findAll: mengembalikan daftar game aktif dan memfilter game yang sudah dihapus (soft delete)")
    void findAllActiveGames() {
        Game activeGame = Game.builder().id(UUID.randomUUID()).name("PUBG").deletedAt(null).build();
        Game deletedGame = Game.builder().id(UUID.randomUUID()).name("Old Game").deletedAt(Timestamp.from(Instant.now())).build();
        GameResponse response = new GameResponse(activeGame.getId(), "PUBG", Instant.now());

        when(gameRepository.findAll()).thenReturn(Flux.just(activeGame, deletedGame));
        when(gameMapper.toResponse(activeGame)).thenReturn(response);

        StepVerifier.create(gameService.findAll())
                .expectNext(response)
                .verifyComplete();

        verify(gameMapper, never()).toResponse(deletedGame);
    }

    // Skenario: Menghapus game yang ID-nya valid.
    // Ekspektasi: Soft delete dilakukan dengan mengisi kolom deletedAt waktu sekarang, menyimpan perubahan, dan mengembalikan pesan sukses.
    @Test
    @DisplayName("delete: sukses melakukan soft delete pada game")
    void deleteSuccess() {
        UUID gameId = UUID.randomUUID();
        Game game = Game.builder().id(gameId).name("Valorant").deletedAt(null).build();

        when(gameRepository.findById(gameId)).thenReturn(Mono.just(game));
        when(gameRepository.save(game)).thenReturn(Mono.just(game));

        StepVerifier.create(gameService.delete(gameId.toString()))
                .expectNext("Game berhasil dihapus")
                .verifyComplete();

        assert game.getDeletedAt() != null;
        verify(gameRepository).save(game);
    }

    // Skenario: Mencoba menghapus game dengan ID yang tidak terdaftar.
    // Ekspektasi: Gagal, melempar IllegalArgumentException ("Game tidak ditemukan").
    @Test
    @DisplayName("delete: gagal ketika game yang akan dihapus tidak ditemukan")
    void deleteNotFound() {
        UUID gameId = UUID.randomUUID();

        when(gameRepository.findById(gameId)).thenReturn(Mono.empty());

        StepVerifier.create(gameService.delete(gameId.toString()))
                .expectErrorMatches(err -> err instanceof IllegalArgumentException && err.getMessage().equals("Game tidak ditemukan"))
                .verify();
    }
}