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
@NoArgsConstructor
@AllArgsConstructor
@Table("player")
public class Player {

    @Id
    private UUID id;

    @Column("name")
    private String name;

    @Column("email")
    private String email;

    @Column("created_at")
    private Timestamp createdAt;

    @Column("last_login")
    private Timestamp lastLogin;

    @Column("profile_image")
    private String profileImage;

    @Column("location")
    private String location;

    @Column("game_id")
    private UUID gameId;

}