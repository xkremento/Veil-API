package com.tfg.veilapi.security

import com.tfg.veilapi.repository.PlayerRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
class JwtUserDetailsService(private val playerRepository: PlayerRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val player = playerRepository.findById(username)
            .orElseThrow { UsernameNotFoundException("User not found with email: $username") }

        // Convert roles into authorities
        val authorities = player.roles.stream()
            .map { role -> SimpleGrantedAuthority("ROLE_${role.name}") }
            .collect(Collectors.toList())

        // If it doesn't have roles, add the default USER role
        if (authorities.isEmpty()) {
            authorities.add(SimpleGrantedAuthority("ROLE_USER"))
        }

        return User.builder()
            .username(player.email)
            .password(player.password)
            .authorities(authorities)
            .build()
    }
}