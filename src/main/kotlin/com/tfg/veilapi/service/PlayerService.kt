package com.tfg.veilapi.service

import com.tfg.veilapi.dto.PlayerRegistrationDTO
import com.tfg.veilapi.dto.PlayerResponseDTO
import com.tfg.veilapi.dto.PlayerUpdateDTO
import com.tfg.veilapi.model.Player
import com.tfg.veilapi.repository.PlayerRepository
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class PlayerService(
    private val playerRepository: PlayerRepository,
    private val passwordEncoder: PasswordEncoder,
    private val roleService: RoleService
) {
    companion object {
        const val MAX_COINS = 999999 // Maximum limit of 6 digits
    }

    @Transactional
    fun registerPlayer(playerDto: PlayerRegistrationDTO): PlayerResponseDTO {
        if (playerRepository.existsById(playerDto.email)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Player with this email already exists")
        }

        val player = Player(
            email = playerDto.email,
            nickname = playerDto.nickname,
            password = passwordEncoder.encode(playerDto.password),
            skinUrl = playerDto.skinUrl
        )

        // Add USER role by default
        player.roles.add(roleService.findByName("USER"))

        val savedPlayer = playerRepository.save(player)
        return convertToResponseDTO(savedPlayer)
    }

    // Method to add admin role to a player
    @Transactional
    fun addAdminRole(email: String): PlayerResponseDTO {
        val player = findPlayerByEmail(email)
        player.roles.add(roleService.findByName("ADMIN"))
        val savedPlayer = playerRepository.save(player)
        return convertToResponseDTO(savedPlayer)
    }

    // Method to remove admin role from a player
    @Transactional
    fun removeAdminRole(email: String): PlayerResponseDTO {
        val player = findPlayerByEmail(email)
        player.roles.removeIf { it.name == "ADMIN" }
        val savedPlayer = playerRepository.save(player)
        return convertToResponseDTO(savedPlayer)
    }

    // Method for admins to add coins to any player
    @Transactional
    fun adminAddCoinsToPlayer(playerEmail: String, amount: Int): PlayerResponseDTO {
        val player = findPlayerByEmail(playerEmail)

        if (amount <= 0) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be positive")
        }

        val newCoinsAmount = player.coins + amount
        if (newCoinsAmount > MAX_COINS) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Player cannot have more than $MAX_COINS coins")
        }

        val updatedPlayer = player.copy(
            coins = newCoinsAmount
        )

        val savedPlayer = playerRepository.save(updatedPlayer)
        return convertToResponseDTO(savedPlayer)
    }

    @Transactional(readOnly = true)
    fun getPlayer(email: String): PlayerResponseDTO {
        val player = findPlayerByEmail(email)
        return convertToResponseDTO(player)
    }

    @Transactional
    fun updatePlayer(email: String, updateDto: PlayerUpdateDTO): PlayerResponseDTO {
        val player = findPlayerByEmail(email)

        val updatedPlayer = player.copy(
            nickname = updateDto.nickname ?: player.nickname,
            password = updateDto.password?.let { passwordEncoder.encode(it) } ?: player.password,
            skinUrl = updateDto.skinUrl ?: player.skinUrl
        )

        val savedPlayer = playerRepository.save(updatedPlayer)
        return convertToResponseDTO(savedPlayer)
    }

    // Original method to add coins - kept for potential internal use
    // but not exposed through public endpoints
    @Transactional
    protected fun addCoinsToPlayer(email: String, amount: Int): PlayerResponseDTO {
        val player = findPlayerByEmail(email)

        if (amount <= 0) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be positive")
        }

        val newCoinsAmount = player.coins + amount
        if (newCoinsAmount > MAX_COINS) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Player cannot have more than $MAX_COINS coins")
        }

        val updatedPlayer = player.copy(
            coins = newCoinsAmount
        )

        val savedPlayer = playerRepository.save(updatedPlayer)
        return convertToResponseDTO(savedPlayer)
    }

    @Transactional
    fun deletePlayer(email: String) {
        if (!playerRepository.existsById(email)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found")
        }
        playerRepository.deleteById(email)
    }

    @Transactional(readOnly = true)
    fun findPlayerByEmail(email: String): Player {
        return playerRepository.findById(email)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found") }
    }

    fun convertToResponseDTO(player: Player): PlayerResponseDTO {
        return PlayerResponseDTO(
            email = player.email,
            nickname = player.nickname,
            coins = player.coins,
            skinUrl = player.skinUrl
        )
    }
}