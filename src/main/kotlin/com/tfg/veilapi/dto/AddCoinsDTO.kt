package com.tfg.veilapi.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class AddCoinsDTO(
    @field:NotNull
    @field:Min(1)
    val amount: Int
)