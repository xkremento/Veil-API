package com.tfg.veilapi.model

import jakarta.persistence.*
import lombok.EqualsAndHashCode

@Entity
data class FriendRequest(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "requester_email")
    val requester: Player = Player(),

    @ManyToOne
    @JoinColumn(name = "player_email")
    val player: Player = Player()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FriendRequest

        if (id != 0L && other.id != 0L) {
            return id == other.id
        }
        return false
    }

    override fun hashCode(): Int {
        return if (id != 0L) id.hashCode() else System.identityHashCode(this)
    }

    override fun toString(): String {
        return "FriendRequest(id=$id)"
    }
}