package com.tfg.veilapi.model

import jakarta.persistence.*
import lombok.EqualsAndHashCode
import java.time.LocalDateTime

@Entity
data class PlayerGame(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "player_email")
    val player: Player = Player(),

    @ManyToOne
    @JoinColumn(name = "game_id")
    val game: Game = Game(),

    @Enumerated(EnumType.STRING)  // Guarda el enum como STRING en la BD
    val role: GameRole = GameRole.INNOCENT,

    val gameDateTime: LocalDateTime = LocalDateTime.now()
)