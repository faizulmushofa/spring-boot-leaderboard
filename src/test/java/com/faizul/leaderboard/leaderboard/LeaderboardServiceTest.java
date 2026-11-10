package com.faizul.leaderboard.leaderboard;

import com.faizul.leaderboard.dtos.model.leaderboard.LeaderboardScoreRequest;
import com.faizul.leaderboard.dtos.model.leaderboard.LeaderboardResponse;
import com.faizul.leaderboard.dtos.model.player.PlayerResponse;
import com.faizul.leaderboard.mapper.LeaderBoardMapper;
import com.faizul.leaderboard.models.Leaderboard;
import com.faizul.leaderboard.repositories.model.LeaderboardRepository;
import com.faizul.leaderboard.services.model.LeaderboardService;
import com.faizul.leaderboard.services.model.PlayerServiceImp;
import com.faizul.leaderboard.services.redis.LeaderboardCacheService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LeaderboardServiceTest {

    @Mock
    LeaderboardRepository leaderboardRepository;

    @Mock
    LeaderboardCacheService leaderboardCacheService;

    @Mock
    PlayerServiceImp playerService;

    @Mock
    LeaderBoardMapper leaderboardMapper;

    @InjectMocks
    LeaderboardService leaderboardService;

    // Skenario: Player pertama kali submit skor pada game tertentu (data belum ada di database).
    // Ekspektasi: Skor disimpan sebagai record baru di PostgreSQL, disinkronkan ke Redis ZSet, dan mengembalikan LeaderboardResponse.
    @Test
    @DisplayName("recordScore: insert baru saat skor belum ada di database")
    void recordScoreNewPlayer() {
        UUID gameId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();
        LeaderboardScoreRequest request = new LeaderboardScoreRequest(gameId, playerId, 100);

        Leaderboard entity = Leaderboard.builder()
                .id(UUID.randomUUID())
                .gameId(gameId)
                .playerId(playerId)
                .score(100)
                .createdAt(Timestamp.from(Instant.now()))
                .build();

        LeaderboardResponse response = new LeaderboardResponse(
                entity.getId(), gameId, playerId, 100, entity.getCreatedAt()
        );

        when(leaderboardRepository.findByGameIdAndPlayerId(gameId, playerId)).thenReturn(Mono.empty());
        when(leaderboardMapper.toEntity(request)).thenReturn(entity);
        when(leaderboardRepository.save(any(Leaderboard.class))).thenReturn(Mono.just(entity));
        when(leaderboardCacheService.addScore(gameId.toString(), playerId.toString(), 100.0)).thenReturn(Mono.just(true));
        when(leaderboardMapper.toResponse(entity)).thenReturn(response);

        StepVerifier.create(leaderboardService.recordScore(request))
                .expectNext(response)
                .verifyComplete();

        verify(leaderboardRepository).findByGameIdAndPlayerId(gameId, playerId);
        verify(leaderboardRepository).save(any(Leaderboard.class));
        verify(leaderboardCacheService).addScore(gameId.toString(), playerId.toString(), 100.0);
    }

    // Skenario: Player sudah punya skor lama (misal: 100), lalu submit skor baru yang lebih tinggi (200).
    // Ekspektasi: Skor di-update di database SQL, timestamp diperbarui, dan skor di Redis ZSet ikut di-update.
    @Test
    @DisplayName("recordScore: update saat skor baru lebih tinggi dari sebelumnya")
    void recordScoreUpdateHigherScore() {
        UUID gameId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();
        LeaderboardScoreRequest request = new LeaderboardScoreRequest(gameId, playerId, 200);

        Leaderboard existing = Leaderboard.builder()
                .id(UUID.randomUUID())
                .gameId(gameId)
                .playerId(playerId)
                .score(100)
                .createdAt(Timestamp.from(Instant.now()))
                .build();

        Leaderboard updated = Leaderboard.builder()
                .id(existing.getId())
                .gameId(gameId)
                .playerId(playerId)
                .score(200)
                .createdAt(Timestamp.from(Instant.now()))
                .build();

        LeaderboardResponse response = new LeaderboardResponse(
                updated.getId(), gameId, playerId, 200, updated.getCreatedAt()
        );

        when(leaderboardRepository.findByGameIdAndPlayerId(gameId, playerId)).thenReturn(Mono.just(existing));
        when(leaderboardRepository.save(existing)).thenReturn(Mono.just(updated));
        when(leaderboardCacheService.addScore(gameId.toString(), playerId.toString(), 200.0)).thenReturn(Mono.just(true));
        when(leaderboardMapper.toResponse(updated)).thenReturn(response);

        StepVerifier.create(leaderboardService.recordScore(request))
                .expectNext(response)
                .verifyComplete();

        verify(leaderboardRepository).save(existing);
        verify(leaderboardCacheService).addScore(gameId.toString(), playerId.toString(), 200.0);
    }

    // Skenario: Request submit score bernilai null.
    // Ekspektasi: Validasi gagal seketika sebelum menyentuh DB, melempar IllegalArgumentException ("Request tidak boleh kosong").
    @Test
    @DisplayName("recordScore: gagal jika request null")
    void recordScoreNullRequest() {
        StepVerifier.create(leaderboardService.recordScore(null))
                .expectErrorMatches(err -> err instanceof IllegalArgumentException && err.getMessage().equals("Request tidak boleh kosong"))
                .verify();
    }

    // Skenario (Cache Hit): Ambil Top-N leaderboard di mana data sudah tersedia lengkap di Redis ZSet.
    // Ekspektasi: Ambil data ranking & score dari ZSet, lalu enrich nama & avatar via PlayerService tanpa menyentuh SQL database sama sekali.
    @Test
    @DisplayName("getTopLeaderboard: cache hit — ambil data ranking dari Redis dan enrich profil via playerService")
    void getTopLeaderboardCacheHit() {
        UUID gameId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        ZSetOperations.TypedTuple<String> tuple = new DefaultTypedTuple<>(playerId.toString(), 150.0);
        PlayerResponse playerResponse = new PlayerResponse(
                playerId, "Faizul", "avatar.png", "Jakarta", Instant.now(), Instant.now(), gameId
        );

        when(leaderboardCacheService.getTopWithScore(gameId.toString(), 1L))
                .thenReturn(Flux.just(tuple));
        when(playerService.findById(playerId.toString()))
                .thenReturn(Mono.just(playerResponse));

        StepVerifier.create(leaderboardService.getTopLeaderboard(gameId, 1L))
                .assertNext(entry -> {
                    assert entry.rank().equals(1L);
                    assert entry.playerId().equals(playerId);
                    assert entry.playerName().equals("Faizul");
                    assert entry.score().equals(150.0);
                })
                .verifyComplete();

        verify(leaderboardCacheService).getTopWithScore(gameId.toString(), 1L);
        verify(playerService).findById(playerId.toString());
        verifyNoInteractions(leaderboardRepository);
    }

    // Skenario: Mengambil data peringkat personal seorang player (misal player berada di posisi index 0 di Redis).
    // Ekspektasi: Skor dan rank diambil dari Redis ZSet, rank 0-based dikonversi menjadi 1-based (rank = 1), profil digabung dari PlayerService.
    @Test
    @DisplayName("getPlayerRank: berhasil mengembalikan rank (1-based), score, dan info profil player")
    void getPlayerRank() {
        UUID gameId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        PlayerResponse playerResponse = new PlayerResponse(
                playerId, "Faizul", "avatar.png", "Jakarta", Instant.now(), Instant.now(), gameId
        );

        when(leaderboardCacheService.getRank(gameId.toString(), playerId.toString())).thenReturn(Mono.just(0L));
        when(leaderboardCacheService.getScore(gameId.toString(), playerId.toString())).thenReturn(Mono.just(300.0));
        when(playerService.findById(playerId.toString())).thenReturn(Mono.just(playerResponse));

        StepVerifier.create(leaderboardService.getPlayerRank(gameId, playerId))
                .assertNext(entry -> {
                    assert entry.rank().equals(1L);
                    assert entry.score().equals(300.0);
                    assert entry.playerName().equals("Faizul");
                })
                .verifyComplete();
    }
}