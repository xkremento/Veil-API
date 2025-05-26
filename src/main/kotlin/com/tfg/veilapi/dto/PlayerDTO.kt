package com.tfg.veilapi.dto

import jakarta.validation.constraints.*

data class PlayerRegistrationDTO(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email format is invalid")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
        message = "Email must have a valid domain"
    )
    @field:Size(max = 254, message = "Email cannot exceed 254 characters")
    val email: String,

    @field:NotBlank(message = "Nickname is required")
    @field:Size(min = 3, max = 30, message = "Nickname must be between 3 and 30 characters")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9_-]+$",
        message = "Nickname can only contain letters, numbers, underscores and hyphens"
    )
    val nickname: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
        message = "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character"
    )
    val password: String,

    @field:Pattern(
        regexp = "^(https?://.+|)$",
        message = "Skin URL must be a valid HTTP/HTTPS URL or empty"
    )
    @field:Size(max = 2048, message = "Skin URL cannot exceed 2048 characters")
    val skinUrl: String? = null,

    @field:Pattern(
        regexp = "^(https?://.+|)$",
        message = "Profile image URL must be a valid HTTP/HTTPS URL or empty"
    )
    @field:Size(max = 2048, message = "Profile image URL cannot exceed 2048 characters")
    val profileImageUrl: String? = null
)

data class PlayerResponseDTO(
    val email: String, val nickname: String, val coins: Int, val skinUrl: String?, val profileImageUrl: String?
)

data class PlayerUpdateDTO(
    @field:Size(min = 3, max = 30, message = "Nickname must be between 3 and 30 characters")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9_-]+$",
        message = "Nickname can only contain letters, numbers, underscores and hyphens"
    )
    val nickname: String? = null,

    @field:Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
        message = "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character"
    )
    val password: String? = null
)