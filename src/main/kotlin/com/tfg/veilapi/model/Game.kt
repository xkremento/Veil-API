package com.tfg.veilapi.model

import jakarta.persistence.*
import lombok.EqualsAndHashCode

@Entity
data class Game(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val duration: Int = 0,

    @OneToMany(mappedBy = "game", cascade = [CascadeType.ALL], orphanRemoval = true)
    val playerGames: MutableSet<PlayerGame> = mutableSetOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Game

        if (id != 0L && other.id != 0L) {
            return id == other.id
        }
        return false  // Consideramos entidades sin ID persistido como diferentes
    }

    override fun hashCode(): Int {
        return if (id != 0L) id.hashCode() else System.identityHashCode(this)
    }

    override fun toString(): String {
        return "Game(id=$id, duration=$duration)"
    }
}