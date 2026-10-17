package com.faizul.leaderboard.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("friends")
public class Friendship {

    @Id
    private UUID id;

    @Column("player_id")
    private UUID player_id;

    @Column("friend_id")
    private UUID friend_id;

}
