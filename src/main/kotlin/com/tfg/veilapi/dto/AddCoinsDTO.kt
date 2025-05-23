package com.tfg.veilapi.dto

import com.tfg.veilapi.config.ValidationConstants
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class AddCoinsDTO(
    @field:NotNull(message = "Amount is required")
    @field:Min(value = ValidationConstants.MIN_COINS_AMOUNT.toLong(), message = "Amount must be at least ${ValidationConstants.MIN_COINS_AMOUNT}")
    @field:Max(value = ValidationConstants.MAX_COINS_AMOUNT.toLong(), message = "Amount cannot exceed ${ValidationConstants.MAX_COINS_AMOUNT}")
    val amount: Int
)