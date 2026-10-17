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
    private UUID id; //4 byte

    @Column("name")
    private String name; //4 byte

    @Column("email")
    private String email;

    @Column("created_at")
    private Timestamp created_at;

    @Column("last_login")
    private Timestamp last_login;

    @Column("profile_image")
    private String profile_image;

    @Column("location")
    private String location;

    @Column("game_id")
    private UUID game_id;

}
