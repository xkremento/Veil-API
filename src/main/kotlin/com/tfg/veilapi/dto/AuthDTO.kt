package com.tfg.veilapi.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class AuthRequestDTO(
    @field:Email
    val email: String,

    @field:NotBlank
    val password: String
)

data class AuthResponseDTO(
    val token: String,
    val email: String,
    val nickname: String
)