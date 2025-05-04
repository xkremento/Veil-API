package com.tfg.veilapi.repository

import com.tfg.veilapi.model.FriendRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FriendRequestRepository : JpaRepository<FriendRequest, Long> {
    fun findByRequesterEmailAndPlayerEmail(requesterEmail: String, playerEmail: String): FriendRequest?
    fun findByPlayerEmail(playerEmail: String): List<FriendRequest>
}