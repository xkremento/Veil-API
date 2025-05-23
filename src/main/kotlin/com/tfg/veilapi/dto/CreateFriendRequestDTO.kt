package com.tfg.veilapi.dto

import com.tfg.veilapi.config.ValidationConstants
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateFriendRequestDTO(
    @field:NotBlank(message = "Requester ID is required")
    @field:Email(message = "Requester ID must be a valid email")
    @field:Size(max = ValidationConstants.EMAIL_MAX_LENGTH, message = "Requester email cannot exceed ${ValidationConstants.EMAIL_MAX_LENGTH} characters")
    val requesterId: String,

    @field:NotBlank(message = "Player ID is required")
    @field:Email(message = "Player ID must be a valid email")
    @field:Size(max = ValidationConstants.EMAIL_MAX_LENGTH, message = "Player email cannot exceed ${ValidationConstants.EMAIL_MAX_LENGTH} characters")
    val playerId: String
)