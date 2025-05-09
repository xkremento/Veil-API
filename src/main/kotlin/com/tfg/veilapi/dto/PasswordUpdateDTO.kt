package com.tfg.veilapi.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class PasswordUpdateDTO(
    @field:NotBlank(message = "Password is required") @field:Size(
        min = 8,
        max = 128,
        message = "Password must be between 8 and 128 characters"
    ) @field:Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
        message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character"
    ) val password: String
)