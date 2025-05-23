package com.tfg.veilapi.dto

import com.tfg.veilapi.config.ValidationConstants
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.URL

data class ProfileImageDTO(
    @field:NotBlank(message = "Profile image URL is required")
    @field:URL(message = "Must be a valid URL")
    @field:Size(max = ValidationConstants.URL_MAX_LENGTH, message = "Profile image URL cannot exceed ${ValidationConstants.URL_MAX_LENGTH} characters")
    val profileImageUrl: String
)