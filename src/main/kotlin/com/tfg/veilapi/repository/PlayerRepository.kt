package com.tfg.veilapi.repository

import com.tfg.veilapi.model.Player
import org.springframework.data.jpa.repository.JpaRepository

interface PlayerRepository : JpaRepository<Player, String> {
    fun existsByNicknameAndEmailNot(nickname: String, email: String): Boolean
    fun findByNickname(nickname: String): Player?
}