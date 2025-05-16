package com.tfg.veilapi.repository

import com.tfg.veilapi.model.PlayerGame
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PlayerGameRepository : JpaRepository<PlayerGame, Long> {
    fun findByPlayerEmail(playerEmail: String): List<PlayerGame>
    fun findByGameId(gameId: Long): List<PlayerGame>
    fun findByPlayerEmailAndGameId(playerEmail: String, gameId: Long): PlayerGame?
}