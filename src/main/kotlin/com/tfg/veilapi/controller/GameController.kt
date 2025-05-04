package com.tfg.veilapi.controller

import com.tfg.veilapi.dto.GameCreationDTO
import com.tfg.veilapi.dto.GameResponseDTO
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

@RestController
@RequestMapping("/api/games")
@Tag(name = "Games", description = "Game management API")
@SecurityRequirement(name = "bearerAuth")
class GameController(private val gameService: GameService) {

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
        )]
    )

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createGame(@RequestBody gameDto: GameCreationDTO): GameResponseDTO {
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
        )]
    )

    @GetMapping("/{gameId}")
    fun getGame(@PathVariable gameId: Long): GameResponseDTO {
        return gameService.getGame(gameId)
    }

    @Operation(summary = "Get player games", description = "Get all games for a specific player")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Player games retrieved successfully",
            content = [Content(array = ArraySchema(schema = Schema(implementation = GameResponseDTO::class)))]
        ), ApiResponse(
            responseCode = "404", description = "Player not found", content = [Content()]
        )]
    )

    @GetMapping("/player/{playerEmail}")
    fun getPlayerGames(@PathVariable playerEmail: String): List<GameResponseDTO> {
        return gameService.getPlayerGames(playerEmail)
    }
}