package com.tfg.veilapi.dto

data class GameCreationDTO(
    val duration: Int, val playerEmails: List<String>, val murdererEmail: String
)

data class GameResponseDTO(
    val id: Long, val duration: Int, val players: List<PlayerGameDTO>
)

data class PlayerGameDTO(
    val playerEmail: String, val playerNickname: String, val isMurderer: Boolean, val gameDateTime: String
)