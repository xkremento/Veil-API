package com.tfg.veilapi.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class AddCoinsDTO(
    @field:NotNull(message = "Amount is required") @field:Min(
        value = 1,
        message = "Amount must be at least 1"
    ) @field:Max(value = 999999, message = "Amount cannot exceed 999999") val amount: Int
)