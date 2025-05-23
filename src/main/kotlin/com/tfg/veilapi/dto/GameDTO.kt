package com.tfg.veilapi.dto

import com.tfg.veilapi.config.ValidationConstants
import jakarta.validation.constraints.*

data class GameCreationDTO(
    @field:NotEmpty(message = "Player emails cannot be empty")
    @field:Size(
        min = 2,
        max = 10,
        message = "Game must have between 2 and 10 players"
    )
    val playerEmails: List<@Email(message = "Invalid email format") @Size(max = ValidationConstants.EMAIL_MAX_LENGTH, message = "Email too long") String>,

    @field:NotBlank(message = "Murderer email is required")
    @field:Email(message = "Invalid email format")
    @field:Size(max = ValidationConstants.EMAIL_MAX_LENGTH, message = "Email cannot exceed ${ValidationConstants.EMAIL_MAX_LENGTH} characters")
    val murdererEmail: String,

    @field:Positive(message = "Duration must be positive")
    @field:Min(value = 60, message = "Game duration must be at least 60 seconds")
    @field:Max(value = 3600, message = "Game duration cannot exceed 3600 seconds (1 hour)")
    val duration: Long
)

data class GameResponseDTO(
    val id: Long,
    val duration: Int,
    val players: List<PlayerGameDTO>
)

data class PlayerGameDTO(
    val playerEmail: String,
    val playerNickname: String,
    val isMurderer: Boolean,
    val gameDateTime: String
)