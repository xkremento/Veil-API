package com.tfg.veilapi.security

import com.tfg.veilapi.repository.PlayerRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class JwtUserDetailsService(private val playerRepository: PlayerRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val player = playerRepository.findById(username)
            .orElseThrow { UsernameNotFoundException("User not found with email: $username") }
        return User.builder()
            .username(player.email)
            .password(player.password)
            .roles("USER")
            .build()
    }
}