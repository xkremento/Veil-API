package com.tfg.veilapi.model

import jakarta.persistence.*
import lombok.EqualsAndHashCode
import java.time.LocalDateTime

@EqualsAndHashCode(onlyExplicitlyIncluded = true, exclude = ["player", "game"])
@Entity
data class PlayerGame(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "player_email")
    val player: Player = Player(),

    @ManyToOne
    @JoinColumn(name = "game_id")
    val game: Game = Game(),

    val playerIsMurderer: Boolean = false,

    val gameDateTime: LocalDateTime = LocalDateTime.now()
)