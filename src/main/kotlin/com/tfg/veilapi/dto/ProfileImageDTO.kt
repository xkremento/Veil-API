package com.tfg.veilapi.dto

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.URL

data class ProfileImageDTO(
    @field:NotBlank(message = "Profile image URL is required") @field:URL(message = "Must be a valid URL") val profileImageUrl: String
)