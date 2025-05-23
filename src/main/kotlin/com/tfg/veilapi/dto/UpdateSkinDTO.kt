package com.tfg.veilapi.dto

import com.tfg.veilapi.config.ValidationConstants
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.URL

data class UpdateSkinDTO(
    @field:NotBlank(message = "Skin URL is required")
    @field:URL(message = "Must be a valid URL")
    @field:Size(max = ValidationConstants.URL_MAX_LENGTH, message = "Skin URL cannot exceed ${ValidationConstants.URL_MAX_LENGTH} characters")
    val skinUrl: String
)