package com.tfg.veilapi.repository

import com.tfg.veilapi.model.Friends
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FriendsRepository : JpaRepository<Friends, Long> {
    fun findByPlayerEmailAndFriendEmail(playerEmail: String, friendEmail: String): Friends?
}