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
@Table("game")
public class Game {

    @Id
    private UUID id;

    @Column("name")
    private String name;

    @Column("created_at")
    private Timestamp created_at;

    @Column("deleted_at")
    private Timestamp deleted_at;

}
