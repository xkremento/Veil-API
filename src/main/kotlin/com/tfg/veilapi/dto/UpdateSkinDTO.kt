package com.tfg.veilapi.dto

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.URL

data class UpdateSkinDTO(
    @field:NotBlank(message = "Skin URL is required")
    @field:URL(message = "Must be a valid URL")
    val skinUrl: String
)