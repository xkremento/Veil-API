package com.tfg.veilapi.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CreateFriendRequestDTO(
    @field:NotBlank(message = "Requester email is required")
    @field:Email(message = "Requester email format is invalid")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
        message = "Requester email must have a valid domain"
    )
    @field:Size(max = 254, message = "Email cannot exceed 254 characters")
    val requesterId: String,

    @field:NotBlank(message = "Player email is required")
    @field:Email(message = "Player email format is invalid")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
        message = "Player email must have a valid domain"
    )
    @field:Size(max = 254, message = "Email cannot exceed 254 characters")
    val playerId: String
)