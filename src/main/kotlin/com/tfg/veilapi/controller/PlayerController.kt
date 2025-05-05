package com.tfg.veilapi.controller

import com.tfg.veilapi.dto.AddCoinsDTO
import com.tfg.veilapi.dto.PlayerResponseDTO
import com.tfg.veilapi.dto.PlayerUpdateDTO
import com.tfg.veilapi.service.AuthorizationService
import com.tfg.veilapi.service.PlayerService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/players")
@Tag(name = "Players", description = "Player management API")
@SecurityRequirement(name = "bearerAuth")
class PlayerController(
    private val playerService: PlayerService, private val authorizationService: AuthorizationService
) {

    @Operation(summary = "Get player details", description = "Retrieves player information by email")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Found the player",
            content = [Content(schema = Schema(implementation = PlayerResponseDTO::class))]
        ), ApiResponse(
            responseCode = "404", description = "Player not found", content = [Content()]
        ), ApiResponse(
            responseCode = "403", description = "Forbidden - Cannot access other player's data", content = [Content()]
        )]
    )
    @GetMapping("/{email}")
    fun getPlayer(@PathVariable email: String): PlayerResponseDTO {
        // Check that the current user has access
        authorizationService.validateUserAccess(email)
        return playerService.getPlayer(email)
    }

    @Operation(summary = "Update player", description = "Updates player information")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Player updated successfully",
            content = [Content(schema = Schema(implementation = PlayerResponseDTO::class))]
        ), ApiResponse(
            responseCode = "404", description = "Player not found", content = [Content()]
        ), ApiResponse(
            responseCode = "403", description = "Forbidden - Cannot modify other player's data", content = [Content()]
        )]
    )
    @PutMapping("/{email}")
    fun updatePlayer(
        @PathVariable email: String, @RequestBody updateDto: PlayerUpdateDTO
    ): PlayerResponseDTO {
        // Check if the current user has access
        authorizationService.validateUserAccess(email)
        return playerService.updatePlayer(email, updateDto)
    }

    @Operation(summary = "Add coins to player", description = "Adds coins to player's account")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Coins added successfully",
            content = [Content(schema = Schema(implementation = PlayerResponseDTO::class))]
        ), ApiResponse(
            responseCode = "404", description = "Player not found", content = [Content()]
        ), ApiResponse(
            responseCode = "400", description = "Invalid amount", content = [Content()]
        ), ApiResponse(
            responseCode = "403", description = "Forbidden - Cannot modify other player's coins", content = [Content()]
        )]
    )
    @PostMapping("/{email}/coins")
    fun addCoinsToPlayer(
        @PathVariable email: String, @RequestBody addCoinsDTO: AddCoinsDTO
    ): PlayerResponseDTO {
        // Ensure the current user has access
        authorizationService.validateUserAccess(email)
        return playerService.addCoinsToPlayer(email, addCoinsDTO.amount)
    }

    @Operation(summary = "Delete player", description = "Deletes a player")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "204", description = "Player deleted successfully", content = [Content()]
        ), ApiResponse(
            responseCode = "404", description = "Player not found", content = [Content()]
        ), ApiResponse(
            responseCode = "403",
            description = "Forbidden - Cannot delete other player's account",
            content = [Content()]
        )]
    )
    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletePlayer(@PathVariable email: String) {
        // Ensure the current user has access
        authorizationService.validateUserAccess(email)
        playerService.deletePlayer(email)
    }
}