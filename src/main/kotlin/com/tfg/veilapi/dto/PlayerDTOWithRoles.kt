package com.tfg.veilapi.dto

data class PlayerResponseWithRolesDTO(
    val email: String,
    val nickname: String,
    val coins: Int,
    val skinUrl: String?,
    val roles: Set<String>
)