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

        // Create game
        val game = Game(duration = gameDto.duration)
        val savedGame = gameRepository.save(game)

        // Add players to game
        val playerGames = gameDto.playerEmails.map { email ->
            val player = playerService.findPlayerByEmail(email)
            PlayerGame(
                player = player,
                game = savedGame,
                // Usar el enum GameRole en lugar de un booleano
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
}