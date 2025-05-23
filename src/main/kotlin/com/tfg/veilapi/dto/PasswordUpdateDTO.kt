package com.tfg.veilapi.dto

import com.tfg.veilapi.config.ValidationConstants
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class PasswordUpdateDTO(
    @field:NotBlank(message = "Password is required")
    @field:Size(
        min = ValidationConstants.PASSWORD_MIN_LENGTH,
        max = ValidationConstants.PASSWORD_MAX_LENGTH,
        message = "Password must be between ${ValidationConstants.PASSWORD_MIN_LENGTH} and ${ValidationConstants.PASSWORD_MAX_LENGTH} characters"
    )
    @field:Pattern(
        regexp = ValidationConstants.PASSWORD_PATTERN,
        message = ValidationConstants.PASSWORD_MESSAGE
    )
    val password: String
)