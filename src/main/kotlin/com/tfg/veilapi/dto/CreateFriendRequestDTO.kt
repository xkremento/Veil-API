package com.tfg.veilapi.dto

import jakarta.validation.constraints.*

data class CreateFriendRequestDTO(
    @field:NotBlank(message = "Player email is required")
    @field:Email(message = "Player email format is invalid")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
        message = "Player email must have a valid domain"
    )
    @field:Size(max = 254, message = "Email cannot exceed 254 characters")
    val playerId: String
)