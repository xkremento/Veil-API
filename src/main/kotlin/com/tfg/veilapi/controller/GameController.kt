package com.tfg.veilapi.controller

import com.tfg.veilapi.dto.GameCreationDTO
import com.tfg.veilapi.dto.GameResponseDTO
import com.tfg.veilapi.service.AuthorizationService
import com.tfg.veilapi.service.GameService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/games")
@Tag(name = "Game Management", description = "Operations for managing games")
class GameController(
    private val gameService: GameService,
    private val authorizationService: AuthorizationService
) {

    @PostMapping
    @Operation(summary = "Create a new game")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Game created successfully"),
        ApiResponse(responseCode = "400", description = "Invalid game data or murderer not in player list"),
        ApiResponse(responseCode = "403", description = "Not authorized to create game with these players")
    ])
    fun createGame(@Valid @RequestBody gameDto: GameCreationDTO): ResponseEntity<GameResponseDTO> {
        val currentUserEmail = authorizationService.getCurrentUserEmail()

        // Verify that the authenticated user is in the list of players
        if (!gameDto.playerEmails.contains(currentUserEmail)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You can only create games where you are a participant")
        }

        // Validate that there are no duplicate emails
        if (gameDto.playerEmails.size != gameDto.playerEmails.toSet().size) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate player emails are not allowed")
        }

        val game = gameService.createGame(gameDto)
        return ResponseEntity(game, HttpStatus.CREATED)
    }

    @GetMapping("/{gameId}")
    @Operation(summary = "Get game details")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Game details retrieved successfully"),
        ApiResponse(responseCode = "403", description = "Not authorized to view this game"),
        ApiResponse(responseCode = "404", description = "Game not found")
    ])
    fun getGame(@PathVariable gameId: Long): ResponseEntity<GameResponseDTO> {
        val currentUserEmail = authorizationService.getCurrentUserEmail()
        val game = gameService.getGame(gameId)

        // Verify that the authenticated user is part of the game
        val isPlayerInGame = game.players.any { it.playerEmail == currentUserEmail }
        if (!isPlayerInGame) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view games you participated in")
        }

        return ResponseEntity.ok(game)
    }

    @GetMapping("/my-games")
    @Operation(summary = "Get games for authenticated user")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "User's games retrieved successfully")
    ])
    fun getMyGames(): ResponseEntity<List<GameResponseDTO>> {
        val currentUserEmail = authorizationService.getCurrentUserEmail()
        val games = gameService.getPlayerGames(currentUserEmail)
        return ResponseEntity.ok(games)
    }

    @GetMapping("/{gameId}/murderer-check")
    @Operation(summary = "Check if authenticated user was murderer in specific game")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Murderer status retrieved successfully"),
        ApiResponse(responseCode = "403", description = "Not authorized to check this game"),
        ApiResponse(responseCode = "404", description = "Game not found or user not in game")
    ])
    fun checkIfMurderer(@PathVariable gameId: Long): ResponseEntity<Map<String, Boolean>> {
        val currentUserEmail = authorizationService.getCurrentUserEmail()

        // Verify that the user is in the game before revealing information
        val game = gameService.getGame(gameId)
        val isPlayerInGame = game.players.any { it.playerEmail == currentUserEmail }
        if (!isPlayerInGame) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You can only check games you participated in")
        }

        val wasMurderer = gameService.wasPlayerMurdererInGame(currentUserEmail, gameId)
        return ResponseEntity.ok(mapOf("wasMurderer" to wasMurderer))
    }

    @PutMapping("/{gameId}/murderer/{playerEmail}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Set player as murderer (Admin only)")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Murderer updated successfully"),
        ApiResponse(responseCode = "400", description = "Player not in game"),
        ApiResponse(responseCode = "403", description = "Admin access required"),
        ApiResponse(responseCode = "404", description = "Game not found")
    ])
    fun setPlayerAsMurderer(
        @PathVariable gameId: Long,
        @PathVariable playerEmail: String
    ): ResponseEntity<GameResponseDTO> {
        val updatedGame = gameService.setPlayerAsMurderer(gameId, playerEmail)
        return ResponseEntity.ok(updatedGame)
    }
}