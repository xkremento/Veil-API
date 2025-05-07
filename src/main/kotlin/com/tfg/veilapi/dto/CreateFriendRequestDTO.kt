package com.tfg.veilapi.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class CreateFriendRequestDTO(
    @field:NotBlank(message = "Requester ID is required") @field:Email(message = "Requester ID must be a valid email") val requesterId: String,

    @field:NotBlank(message = "Player ID is required") @field:Email(message = "Player ID must be a valid email") val playerId: String
)