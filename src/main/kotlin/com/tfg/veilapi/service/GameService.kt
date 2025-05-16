package com.tfg.veilapi.service

import com.tfg.veilapi.dto.GameCreationDTO
import com.tfg.veilapi.dto.GameResponseDTO
import com.tfg.veilapi.dto.PlayerGameDTO
import com.tfg.veilapi.model.Game
import com.tfg.veilapi.model.GameRole
import com.tfg.veilapi.model.PlayerGame
import com.tfg.veilapi.repository.GameRepository
import com.tfg.veilapi.repository.PlayerGameRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class GameService(
    private val gameRepository: GameRepository,
    private val playerGameRepository: PlayerGameRepository,
    private val playerService: PlayerService
) {

    @Transactional
    fun createGame(gameDto: GameCreationDTO): GameResponseDTO {
        if (!gameDto.playerEmails.contains(gameDto.murdererEmail)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Murderer must be one of the players")
        }

        val game = Game(duration = gameDto.duration)
        val savedGame = gameRepository.save(game)

        val playerGames = gameDto.playerEmails.map { email ->
            val player = playerService.findPlayerByEmail(email)
            PlayerGame(
                player = player,
                game = savedGame,
                role = if (email == gameDto.murdererEmail) GameRole.MURDERER else GameRole.INNOCENT,
                gameDateTime = LocalDateTime.now()
            )
        }

        playerGameRepository.saveAll(playerGames)

        return getGame(savedGame.id)
    }

    fun getGame(gameId: Long): GameResponseDTO {
        val game = gameRepository.findById(gameId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found") }

        val playerGames = playerGameRepository.findByGameId(game.id)

        return GameResponseDTO(
            id = game.id, duration = game.duration, players = playerGames.map { pg ->
                PlayerGameDTO(
                    playerEmail = pg.player.email,
                    playerNickname = pg.player.nickname,
                    // Convert enum to boolean
                    isMurderer = pg.role == GameRole.MURDERER,
                    gameDateTime = pg.gameDateTime.format(DateTimeFormatter.ISO_DATE_TIME)
                )
            })
    }

    fun getPlayerGames(playerEmail: String): List<GameResponseDTO> {
        val player = playerService.findPlayerByEmail(playerEmail)
        val playerGames = playerGameRepository.findByPlayerEmail(player.email)

        return playerGames.map { pg ->
            getGame(pg.game.id)
        }.distinct()
    }

    /**
     * Checks if a player was the murderer in a specific game
     *
     * @param playerEmail The email of the player to check
     * @param gameId The ID of the game to check
     * @return True if the player was the murderer, false otherwise
     */
    fun wasPlayerMurdererInGame(playerEmail: String, gameId: Long): Boolean {
        val player = playerService.findPlayerByEmail(playerEmail)

        val playerGame =
            playerGameRepository.findByPlayerEmailAndGameId(player.email, gameId) ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Player was not part of this game"
            )

        return playerGame.role == GameRole.MURDERER
    }

    /**
     * Sets a player as murderer in a game (admin only)
     * Updates the roles accordingly - sets the current murderer to innocent and the new player to murderer
     *
     * @param gameId The ID of the game
     * @param playerEmail The email of the player to set as murderer
     * @return Updated game response DTO
     */
    @Transactional
    fun setPlayerAsMurderer(gameId: Long, playerEmail: String): GameResponseDTO {
        // Find the game
        val game = gameRepository.findById(gameId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found") }

        // Find the player
        playerService.findPlayerByEmail(playerEmail)

        // Find the player's role in the game
        val playerGames = playerGameRepository.findByGameId(game.id)

        // Check if player is part of the game
        val playerGame = playerGames.find { it.player.email == playerEmail } ?: throw ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Player is not part of this game"
        )

        // Find current murderer
        val currentMurderer = playerGames.find { it.role == GameRole.MURDERER }

        // Update roles
        if (currentMurderer != null && currentMurderer.id != playerGame.id) {
            val updatedCurrentMurderer = currentMurderer.copy(
                role = GameRole.INNOCENT
            )
            playerGameRepository.save(updatedCurrentMurderer)
        }

        // Update new murderer
        val updatedPlayerGame = playerGame.copy(
            role = GameRole.MURDERER
        )
        playerGameRepository.save(updatedPlayerGame)

        return getGame(game.id)
    }
}