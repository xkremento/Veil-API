package com.tfg.veilapi.controller

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

    @Operation(summary = "Get current player details", description = "Retrieves authenticated player information")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Found the player",
            content = [Content(schema = Schema(implementation = PlayerResponseDTO::class))]
        ), ApiResponse(
            responseCode = "404", description = "Player not found", content = [Content()]
        )]
    )
    @GetMapping("/me")
    fun getCurrentPlayer(): PlayerResponseDTO {
        val currentUserEmail = authorizationService.getCurrentUserEmail()
        return playerService.getPlayer(currentUserEmail)
    }



    @Operation(summary = "Update current player", description = "Updates authenticated player information")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Player updated successfully",
            content = [Content(schema = Schema(implementation = PlayerResponseDTO::class))]
        ), ApiResponse(
            responseCode = "404", description = "Player not found", content = [Content()]
        )]
    )
    @PutMapping
    fun updateCurrentPlayer(@RequestBody updateDto: PlayerUpdateDTO): PlayerResponseDTO {
        val currentUserEmail = authorizationService.getCurrentUserEmail()
        return playerService.updatePlayer(currentUserEmail, updateDto)
    }

    @Operation(summary = "Delete current player", description = "Deletes authenticated player account")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "204", description = "Player deleted successfully", content = [Content()]
        ), ApiResponse(
            responseCode = "404", description = "Player not found", content = [Content()]
        )]
    )
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteCurrentPlayer() {
        val currentUserEmail = authorizationService.getCurrentUserEmail()
        playerService.deletePlayer(currentUserEmail)
    }
}