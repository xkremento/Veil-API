package com.tfg.veilapi.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class AuthRequestDTO(
    @field:Email(message = "Email must be valid") @field:NotBlank(message = "Email is required") val email: String,

    @field:NotBlank(message = "Password is required") val password: String
)

data class AuthResponseDTO(
    val token: String, val email: String, val nickname: String
)