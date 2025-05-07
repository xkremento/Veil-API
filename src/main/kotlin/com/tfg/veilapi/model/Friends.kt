package com.tfg.veilapi.model

import jakarta.persistence.*
import lombok.EqualsAndHashCode
import java.time.LocalDateTime

@Entity
@Table(name = "friends")
data class Friends(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "player_email")
    val player: Player = Player(),

    @ManyToOne
    @JoinColumn(name = "friend_email")
    val friend: Player = Player(),

    val friendshipDateTime: LocalDateTime = LocalDateTime.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Friends

        if (id != 0L && other.id != 0L) {
            return id == other.id
        }
        return false
    }

    override fun hashCode(): Int {
        return if (id != 0L) id.hashCode() else System.identityHashCode(this)
    }

    override fun toString(): String {
        return "Friends(id=$id, friendshipDateTime=$friendshipDateTime)"
    }
}