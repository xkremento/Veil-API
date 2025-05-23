package com.tfg.veilapi.dto

import com.tfg.veilapi.config.ValidationConstants
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.URL

data class PlayerRegistrationDTO(
    @field:Email(message = "Email must be valid")
    @field:NotBlank(message = "Email is required")
    @field:Size(max = ValidationConstants.EMAIL_MAX_LENGTH, message = "Email cannot exceed ${ValidationConstants.EMAIL_MAX_LENGTH} characters")
    val email: String,

    @field:NotBlank(message = "Nickname is required")
    @field:Size(min = ValidationConstants.NICKNAME_MIN_LENGTH, max = ValidationConstants.NICKNAME_MAX_LENGTH,
        message = "Nickname must be between ${ValidationConstants.NICKNAME_MIN_LENGTH} and ${ValidationConstants.NICKNAME_MAX_LENGTH} characters")
    @field:Pattern(
        regexp = ValidationConstants.NICKNAME_PATTERN,
        message = ValidationConstants.NICKNAME_MESSAGE
    )
    val nickname: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = ValidationConstants.PASSWORD_MIN_LENGTH, max = ValidationConstants.PASSWORD_MAX_LENGTH,
        message = "Password must be between ${ValidationConstants.PASSWORD_MIN_LENGTH} and ${ValidationConstants.PASSWORD_MAX_LENGTH} characters")
    @field:Pattern(
        regexp = ValidationConstants.PASSWORD_PATTERN,
        message = ValidationConstants.PASSWORD_MESSAGE
    )
    val password: String,

    @field:URL(message = "Skin URL must be a valid URL")
    @field:Size(max = ValidationConstants.URL_MAX_LENGTH, message = "Skin URL cannot exceed ${ValidationConstants.URL_MAX_LENGTH} characters")
    val skinUrl: String? = null,

    @field:URL(message = "Profile image URL must be a valid URL")
    @field:Size(max = ValidationConstants.URL_MAX_LENGTH, message = "Profile image URL cannot exceed ${ValidationConstants.URL_MAX_LENGTH} characters")
    val profileImageUrl: String? = null
)

data class PlayerResponseDTO(
    val email: String,
    val nickname: String,
    val coins: Int,
    val skinUrl: String?,
    val profileImageUrl: String?
)

data class PlayerUpdateDTO(
    @field:Size(
        min = ValidationConstants.NICKNAME_MIN_LENGTH,
        max = ValidationConstants.NICKNAME_MAX_LENGTH,
        message = "Nickname must be between ${ValidationConstants.NICKNAME_MIN_LENGTH} and ${ValidationConstants.NICKNAME_MAX_LENGTH} characters"
    )
    @field:Pattern(
        regexp = ValidationConstants.NICKNAME_PATTERN,
        message = ValidationConstants.NICKNAME_MESSAGE
    )
    val nickname: String? = null,

    @field:Size(min = ValidationConstants.PASSWORD_MIN_LENGTH, max = ValidationConstants.PASSWORD_MAX_LENGTH,
        message = "Password must be between ${ValidationConstants.PASSWORD_MIN_LENGTH} and ${ValidationConstants.PASSWORD_MAX_LENGTH} characters")
    @field:Pattern(
        regexp = ValidationConstants.PASSWORD_PATTERN,
        message = ValidationConstants.PASSWORD_MESSAGE
    )
    val password: String? = null
)
