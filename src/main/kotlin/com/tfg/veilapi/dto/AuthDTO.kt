package com.tfg.veilapi.dto

import com.tfg.veilapi.config.ValidationConstants
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AuthRequestDTO(
    @field:Email(message = "Email must be valid")
    @field:NotBlank(message = "Email is required")
    @field:Size(max = ValidationConstants.EMAIL_MAX_LENGTH, message = "Email cannot exceed ${ValidationConstants.EMAIL_MAX_LENGTH} characters")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = ValidationConstants.PASSWORD_MIN_LENGTH, max = ValidationConstants.PASSWORD_MAX_LENGTH,
        message = "Password must be between ${ValidationConstants.PASSWORD_MIN_LENGTH} and ${ValidationConstants.PASSWORD_MAX_LENGTH} characters")
    val password: String
)

data class AuthResponseDTO(
    val token: String,
    val email: String,
    val nickname: String
)