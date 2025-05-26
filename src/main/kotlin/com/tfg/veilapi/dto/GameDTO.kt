package com.tfg.veilapi.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class GameCreationDTO(
    @field:Min(value = 60, message = "Game duration must be at least 60 seconds")
    @field:Max(value = 7200, message = "Game duration cannot exceed 2 hours (7200 seconds)")
    val duration: Int,

    @field:NotEmpty(message = "Player list cannot be empty")
    @field:Size(min = 3, max = 10, message = "Game must have between 3 and 10 players")
    @field:Valid
    val playerEmails: List<
            @Email(message = "All player emails must be valid")
            @Pattern(
                regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                message = "All player emails must have valid domains"
            )
            @Size(max = 254, message = "Email cannot exceed 254 characters")
            String
            >,

    @field:NotBlank(message = "Murderer email is required")
    @field:Email(message = "Murderer email format is invalid")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
        message = "Murderer email must have a valid domain"
    )
    @field:Size(max = 254, message = "Email cannot exceed 254 characters")
    val murdererEmail: String
)


data class GameResponseDTO(
    val id: Long, val duration: Int, val players: List<PlayerGameDTO>
)

data class PlayerGameDTO(
    val playerEmail: String, val playerNickname: String, val isMurderer: Boolean, val gameDateTime: String
)