package com.tfg.veilapi.model

import jakarta.persistence.*
import lombok.EqualsAndHashCode
import java.time.LocalDateTime

@EqualsAndHashCode(onlyExplicitlyIncluded = true, exclude = ["player", "friend"])
@Entity
@Table(name = "friends")
data class Friends(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "player_email")
    val player: Player = Player(),

    @ManyToOne
    @JoinColumn(name = "friend_email")
    val friend: Player = Player(),

    val friendshipDateTime: LocalDateTime = LocalDateTime.now()
)