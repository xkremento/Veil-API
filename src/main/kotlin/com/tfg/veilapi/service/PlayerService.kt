package com.tfg.veilapi.service

import com.tfg.veilapi.dto.PlayerRegistrationDTO
import com.tfg.veilapi.dto.PlayerResponseDTO
import com.tfg.veilapi.dto.PlayerUpdateDTO
import com.tfg.veilapi.model.Player
import com.tfg.veilapi.repository.PlayerRepository
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class PlayerService(
    private val playerRepository: PlayerRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun registerPlayer(playerDto: PlayerRegistrationDTO): PlayerResponseDTO {
        if (playerRepository.existsById(playerDto.email)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Player with this email already exists")
        }

        val player = Player(
            email = playerDto.email,
            nickname = playerDto.nickname,
            password = passwordEncoder.encode(playerDto.password), // Encrypt password
            skinUrl = playerDto.skinUrl
        )

        val savedPlayer = playerRepository.save(player)
        return convertToResponseDTO(savedPlayer)
    }

    fun getPlayer(email: String): PlayerResponseDTO {
        val player = findPlayerByEmail(email)
        return convertToResponseDTO(player)
    }

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

    fun addCoinsToPlayer(email: String, amount: Int): PlayerResponseDTO {
        val player = findPlayerByEmail(email)

        if (amount <= 0) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be positive")
        }

        val updatedPlayer = player.copy(
            coins = player.coins + amount
        )

        val savedPlayer = playerRepository.save(updatedPlayer)
        return convertToResponseDTO(savedPlayer)
    }

    fun deletePlayer(email: String) {
        if (!playerRepository.existsById(email)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found")
        }
        playerRepository.deleteById(email)
    }

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