package com.tfg.veilapi.controller

import com.tfg.veilapi.dto.GameCreationDTO
import com.tfg.veilapi.dto.GameResponseDTO
import com.tfg.veilapi.service.AuthorizationService
import com.tfg.veilapi.service.GameService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/games")
@Tag(name = "Games", description = "Game management API")
@SecurityRequirement(name = "bearerAuth")
class GameController(
    private val gameService: GameService, private val authorizationService: AuthorizationService
) {

    @Operation(summary = "Create game", description = "Create a new game with players")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "201",
            description = "Game created successfully",
            content = [Content(schema = Schema(implementation = GameResponseDTO::class))]
        ), ApiResponse(
            responseCode = "400",
            description = "Invalid request - murderer must be one of the players",
            content = [Content()]
        ), ApiResponse(
            responseCode = "404", description = "Player not found", content = [Content()]
        ), ApiResponse(
            responseCode = "403", description = "Forbidden - User must be one of the players", content = [Content()]
        )]
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createGame(@RequestBody gameDto: GameCreationDTO): GameResponseDTO {
        // Verify that the current user is included in the game players
        val currentUserEmail = authorizationService.getCurrentUserEmail()
        if (!gameDto.playerEmails.contains(currentUserEmail)) {
            throw ResponseStatusException(
                HttpStatus.FORBIDDEN, "You cannot create a game you are not participating in."
            )
        }
        return gameService.createGame(gameDto)
    }

    @Operation(summary = "Get game", description = "Get details of a specific game")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Game details retrieved successfully",
            content = [Content(schema = Schema(implementation = GameResponseDTO::class))]
        ), ApiResponse(
            responseCode = "404", description = "Game not found", content = [Content()]
        ), ApiResponse(
            responseCode = "403",
            description = "Forbidden - User must be a participant in the game",
            content = [Content()]
        )]
    )
    @GetMapping("/{gameId}")
    fun getGame(@PathVariable gameId: Long): GameResponseDTO {
        val game = gameService.getGame(gameId)
        // Verify that the current user is one of the players
        val currentUserEmail = authorizationService.getCurrentUserEmail()
        val playerEmails = game.players.map { it.playerEmail }
        if (!playerEmails.contains(currentUserEmail)) {
            throw ResponseStatusException(
                HttpStatus.FORBIDDEN, "You do not have access to this game."
            )
        }
        return game
    }

    @Operation(summary = "Get player games", description = "Get all games for a specific player")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Player games retrieved successfully",
            content = [Content(array = ArraySchema(schema = Schema(implementation = GameResponseDTO::class)))]
        ), ApiResponse(
            responseCode = "404", description = "Player not found", content = [Content()]
        ), ApiResponse(
            responseCode = "403", description = "Forbidden - Cannot view games for other players", content = [Content()]
        )]
    )
    @GetMapping("/player/{playerEmail}")
    fun getPlayerGames(@PathVariable playerEmail: String): List<GameResponseDTO> {
        // Verify that the current user is the one querying their games
        authorizationService.validateUserAccess(playerEmail)
        return gameService.getPlayerGames(playerEmail)
    }
}