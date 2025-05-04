package com.tfg.veilapi.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class PlayerRegistrationDTO(
    @field:Email
    val email: String,

    @field:NotBlank
    val nickname: String,

    @field:NotBlank
    val password: String,

    val skinUrl: String? = null
)

data class PlayerResponseDTO(
    val email: String,
    val nickname: String,
    val coins: Int,
    val skinUrl: String?
)

data class PlayerUpdateDTO(
    val nickname: String? = null,
    val password: String? = null,
    val skinUrl: String? = null
)