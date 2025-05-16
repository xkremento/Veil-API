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

        val currentUserEmail = authorizationService.getCurrentUserEmail()
        val playerEmails = game.players.map { it.playerEmail }
        if (!playerEmails.contains(currentUserEmail)) {
            throw ResponseStatusException(
                HttpStatus.FORBIDDEN, "You do not have access to this game."
            )
        }
        return game
    }

    @Operation(summary = "Get my games", description = "Get all games for the authenticated player")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Player games retrieved successfully",
            content = [Content(array = ArraySchema(schema = Schema(implementation = GameResponseDTO::class)))]
        )]
    )
    @GetMapping
    fun getMyGames(): List<GameResponseDTO> {
        val currentUserEmail = authorizationService.getCurrentUserEmail()
        return gameService.getPlayerGames(currentUserEmail)
    }

    @Operation(
        summary = "Check if player was murderer",
        description = "Check if the authenticated player was the murderer in a specific game"
    )
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Role check completed",
            content = [Content(schema = Schema(implementation = Map::class))]
        ), ApiResponse(
            responseCode = "404", description = "Game not found or player not in game", content = [Content()]
        ), ApiResponse(
            responseCode = "403", description = "Forbidden - Can only check your own status", content = [Content()]
        )]
    )
    @GetMapping("/{gameId}/was-murderer")
    fun checkIfPlayerWasMurderer(@PathVariable gameId: Long): Map<String, Boolean> {
        val currentUserEmail = authorizationService.getCurrentUserEmail()

        val game = gameService.getGame(gameId)
        val playerEmails = game.players.map { it.playerEmail }

        if (!playerEmails.contains(currentUserEmail)) {
            throw ResponseStatusException(
                HttpStatus.FORBIDDEN, "You cannot check role in a game you did not participate in."
            )
        }

        val wasMurderer = gameService.wasPlayerMurdererInGame(currentUserEmail, gameId)
        return mapOf("wasMurderer" to wasMurderer)
    }
}