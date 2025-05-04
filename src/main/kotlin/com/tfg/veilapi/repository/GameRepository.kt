package com.tfg.veilapi.repository

import com.tfg.veilapi.model.Game
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GameRepository : JpaRepository<Game, Long>