package com.tfg.veilapi.config

object ValidationConstants {
    // Password validation
    const val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=!]).*$"
    const val PASSWORD_MIN_LENGTH = 8
    const val PASSWORD_MAX_LENGTH = 128
    const val PASSWORD_MESSAGE = "Password must contain at least one digit, one lowercase, one uppercase, and one special character"

    // Nickname validation
    const val NICKNAME_PATTERN = "^[a-zA-Z0-9_]+\$"
    const val NICKNAME_MIN_LENGTH = 3
    const val NICKNAME_MAX_LENGTH = 30
    const val NICKNAME_MESSAGE = "Nickname can only contain letters, numbers and underscores"

    // Email validation
    const val EMAIL_MAX_LENGTH = 254

    // Coins validation
    const val MIN_COINS_AMOUNT = 1
    const val MAX_COINS_AMOUNT = 999999

    // URL validation
    const val URL_MAX_LENGTH = 2048
}