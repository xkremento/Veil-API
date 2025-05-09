package com.tfg.veilapi.model

import jakarta.persistence.*
import lombok.EqualsAndHashCode
import java.time.LocalDateTime

@Entity
data class PlayerGame(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0,

    @ManyToOne @JoinColumn(name = "player_email") val player: Player = Player(),

    @ManyToOne @JoinColumn(name = "game_id") val game: Game = Game(),

    @Enumerated(EnumType.STRING) val role: GameRole = GameRole.INNOCENT,

    val gameDateTime: LocalDateTime = LocalDateTime.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlayerGame

        if (id != 0L && other.id != 0L) {
            return id == other.id
        }
        return false
    }

    override fun hashCode(): Int {
        return if (id != 0L) id.hashCode() else System.identityHashCode(this)
    }

    override fun toString(): String {
        return "PlayerGame(id=$id, role=$role, gameDateTime=$gameDateTime)"
    }
}