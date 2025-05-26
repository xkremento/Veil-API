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
        const val MAX_COINS = 999999
    }

    @Transactional
    fun registerPlayer(playerDto: PlayerRegistrationDTO): PlayerResponseDTO {
        if (playerRepository.existsById(playerDto.email)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Player with this email already exists")
        }

        if (playerRepository.existsByNickname(playerDto.nickname)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Player with this nickname already exists")
        }

        val player = Player(
            email = playerDto.email,
            nickname = playerDto.nickname,
            password = passwordEncoder.encode(playerDto.password),
            skinUrl = playerDto.skinUrl,
            profileImageUrl = playerDto.profileImageUrl
        )

        // Add USER role by default
        player.roles.add(roleService.findByName("USER"))

        val savedPlayer = playerRepository.save(player)
        return convertToResponseDTO(savedPlayer)
    }

    @Transactional
    fun addAdminRole(email: String): PlayerResponseDTO {
        val player = findPlayerByEmail(email)
        player.roles.add(roleService.findByName("ADMIN"))
        val savedPlayer = playerRepository.save(player)
        return convertToResponseDTO(savedPlayer)
    }

    @Transactional
    fun removeAdminRole(email: String): PlayerResponseDTO {
        val player = findPlayerByEmail(email)
        player.roles.removeIf { it.name == "ADMIN" }
        val savedPlayer = playerRepository.save(player)
        return convertToResponseDTO(savedPlayer)
    }

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
    fun adminUpdatePlayer(email: String, updateDto: PlayerUpdateDTO): PlayerResponseDTO {
        val player = findPlayerByEmail(email)

        // Check nickname uniqueness if nickname is being updated
        updateDto.nickname?.let { newNickname ->
            if (newNickname != player.nickname && playerRepository.existsByNickname(newNickname)) {
                throw ResponseStatusException(HttpStatus.CONFLICT, "Player with this nickname already exists")
            }
        }

        val updatedPlayer = player.copy(
            nickname = updateDto.nickname ?: player.nickname,
            password = updateDto.password?.let { passwordEncoder.encode(it) } ?: player.password)

        val savedPlayer = playerRepository.save(updatedPlayer)
        return convertToResponseDTO(savedPlayer)
    }

    @Transactional
    fun adminUpdateNickname(email: String, nickname: String): PlayerResponseDTO {
        val player = findPlayerByEmail(email)

        if (playerRepository.existsByNickname(nickname)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Player with this nickname already exists")
        }

        val updatedPlayer = player.copy(
            nickname = nickname
        )

        val savedPlayer = playerRepository.save(updatedPlayer)
        return convertToResponseDTO(savedPlayer)
    }

    @Transactional
    fun adminUpdatePlayerProfileImage(email: String, profileImageUrl: String): PlayerResponseDTO {
        val player = findPlayerByEmail(email)

        val updatedPlayer = player.copy(
            profileImageUrl = profileImageUrl
        )

        val savedPlayer = playerRepository.save(updatedPlayer)
        return convertToResponseDTO(savedPlayer)
    }

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
            skinUrl = player.skinUrl,
            profileImageUrl = player.profileImageUrl
        )
    }

    @Transactional
    fun adminUpdatePlayerSkin(email: String, skinUrl: String): PlayerResponseDTO {
        val player = findPlayerByEmail(email)

        val updatedPlayer = player.copy(
            skinUrl = skinUrl
        )

        val savedPlayer = playerRepository.save(updatedPlayer)
        return convertToResponseDTO(savedPlayer)
    }

    @Transactional
    fun changePassword(email: String, newPassword: String): PlayerResponseDTO {
        val player = findPlayerByEmail(email)

        val updatedPlayer = player.copy(
            password = passwordEncoder.encode(newPassword)
        )

        val savedPlayer = playerRepository.save(updatedPlayer)
        return convertToResponseDTO(savedPlayer)
    }

    @Transactional
    fun updateProfileImage(email: String, profileImageUrl: String): PlayerResponseDTO {
        val player = findPlayerByEmail(email)

        val updatedPlayer = player.copy(
            profileImageUrl = profileImageUrl
        )

        val savedPlayer = playerRepository.save(updatedPlayer)
        return convertToResponseDTO(savedPlayer)
    }
}