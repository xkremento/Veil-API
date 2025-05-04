package com.tfg.veilapi.model

import jakarta.persistence.*
import lombok.EqualsAndHashCode

@EqualsAndHashCode(onlyExplicitlyIncluded = true, exclude = ["playerGames"])
@Entity
data class FriendRequest(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "requester_email")
    val requester: Player= Player(),

    @ManyToOne
    @JoinColumn(name = "player_email")
    val player: Player = Player()
)