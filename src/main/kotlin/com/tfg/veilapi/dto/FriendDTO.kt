package com.tfg.veilapi.dto

data class FriendRequestDTO(
    val friendRequestId: Long,
    val requesterId: String,
    val requesterNickname: String,
    val playerId: String
)

data class FriendResponseDTO(
    val email: String, val nickname: String, val friendshipDate: String
)