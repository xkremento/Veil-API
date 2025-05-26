package com.tfg.veilapi.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AuthRequestDTO(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email format is invalid")
    @field:Size(max = 254, message = "Email cannot exceed 254 characters")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 1, max = 128, message = "Password length is invalid")
    val password: String
)

data class AuthResponseDTO(
    val token: String, val email: String, val nickname: String
)