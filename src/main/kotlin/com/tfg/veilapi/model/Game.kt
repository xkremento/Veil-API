package com.tfg.veilapi.model

import jakarta.persistence.*
import lombok.EqualsAndHashCode

@EqualsAndHashCode(onlyExplicitlyIncluded = true, exclude = ["playerGames"])
@Entity
data class Game(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    val id: Long = 0,

    val duration: Int = 0,

    @OneToMany(mappedBy = "game", cascade = [CascadeType.ALL], orphanRemoval = true)
    val playerGames: MutableSet<PlayerGame> = mutableSetOf()
)