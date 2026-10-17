package com.faizul.leaderboard.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("leaderboard")
public class Leaderboard {

    @Id
    private UUID id;

    @Column("score")
    private Integer score;

    @Column("created_at")
    private Timestamp created_at;

    @Column("game_id")
    private UUID game_id;

    @Column("player_id")
    private UUID player_id;

}
