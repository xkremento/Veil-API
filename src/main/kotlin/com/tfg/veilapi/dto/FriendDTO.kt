package com.tfg.veilapi.dto

data class FriendRequestDTO(
    val friendRequestId: Long,
    val requesterId: String,
    val requesterNickname: String,
    val playerId: String,
    val requesterProfileImageUrl: String? = null
)

data class FriendResponseDTO(
    val email: String,
    val nickname: String,
    val friendshipDate: String,
    val profileImageUrl: String? = null,
    val skinUrl: String? = null
)