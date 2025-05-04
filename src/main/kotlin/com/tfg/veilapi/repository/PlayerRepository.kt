package com.tfg.veilapi.repository

import com.tfg.veilapi.model.Player
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PlayerRepository : JpaRepository<Player, String>