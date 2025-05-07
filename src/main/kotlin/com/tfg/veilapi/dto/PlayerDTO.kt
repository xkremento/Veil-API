package com.tfg.veilapi.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.URL

data class PlayerRegistrationDTO(
    @field:Email(message = "Email must be valid") @field:NotBlank(message = "Email is required") val email: String,

    @field:NotBlank(message = "Nickname is required") @field:Size(
        min = 3, max = 30, message = "Nickname must be between 3 and 30 characters"
    ) @field:Pattern(
        regexp = "^[a-zA-Z0-9_]+$", message = "Nickname can only contain letters, numbers and underscores"
    ) val nickname: String,

    @field:NotBlank(message = "Password is required") @field:Size(
        min = 8, max = 128, message = "Password must be between 8 and 128 characters"
    ) @field:Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
        message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character"
    ) val password: String,

    @field:URL(message = "Skin URL must be a valid URL") val skinUrl: String? = null
)

data class PlayerResponseDTO(
    val email: String, val nickname: String, val coins: Int, val skinUrl: String?
)

data class PlayerUpdateDTO(
    @field:Size(
        min = 3,
        max = 30,
        message = "Nickname must be between 3 and 30 characters"
    ) @field:Pattern(
        regexp = "^[a-zA-Z0-9_]+$",
        message = "Nickname can only contain letters, numbers and underscores"
    ) val nickname: String? = null,

    @field:Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters") @field:Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
        message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character"
    ) val password: String? = null,

    @field:URL(message = "Skin URL must be a valid URL") val skinUrl: String? = null
)