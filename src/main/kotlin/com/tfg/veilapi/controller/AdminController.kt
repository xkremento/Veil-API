package com.tfg.veilapi.controller

import com.tfg.veilapi.dto.AddCoinsDTO
import com.tfg.veilapi.dto.PlayerResponseDTO
import com.tfg.veilapi.dto.UpdateSkinDTO
import com.tfg.veilapi.service.PlayerService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Admin operations API")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
class AdminController(private val playerService: PlayerService) {

    @Operation(summary = "Get player details by email", description = "Retrieves player information by email (admin only)")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Found the player",
            content = [Content(schema = Schema(implementation = PlayerResponseDTO::class))]
        ), ApiResponse(
            responseCode = "404", description = "Player not found", content = [Content()]
        ), ApiResponse(
            responseCode = "403", description = "Forbidden - Admin only", content = [Content()]
        )]
    )
    @GetMapping("/players/{email}")
    fun getPlayer(@PathVariable email: String): PlayerResponseDTO {
        return playerService.getPlayer(email)
    }

    @Operation(summary = "Add coins to a player", description = "Adds coins to a specified player (admin only)")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Coins added successfully",
            content = [Content(schema = Schema(implementation = PlayerResponseDTO::class))]
        ), ApiResponse(
            responseCode = "404", description = "Player not found", content = [Content()]
        ), ApiResponse(
            responseCode = "400",
            description = "Invalid amount or exceeds maximum coin limit",
            content = [Content()]
        ), ApiResponse(
            responseCode = "403", description = "Forbidden - Admin only", content = [Content()]
        )]
    )
    @PostMapping("/players/{email}/coins")
    fun addCoinsToPlayer(
        @PathVariable email: String,
        @RequestBody addCoinsDTO: AddCoinsDTO
    ): PlayerResponseDTO {
        return playerService.adminAddCoinsToPlayer(email, addCoinsDTO.amount)
    }

    @Operation(summary = "Assign admin role", description = "Adds admin role to a player (admin only)")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Admin role assigned successfully",
            content = [Content(schema = Schema(implementation = PlayerResponseDTO::class))]
        ), ApiResponse(
            responseCode = "404", description = "Player not found", content = [Content()]
        ), ApiResponse(
            responseCode = "403", description = "Forbidden - Admin only", content = [Content()]
        )]
    )
    @PostMapping("/players/{email}/roles/admin")
    fun addAdminRole(@PathVariable email: String): PlayerResponseDTO {
        return playerService.addAdminRole(email)
    }

    @Operation(summary = "Update player skin", description = "Updates a player's skin URL (admin only)")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Skin updated successfully",
            content = [Content(schema = Schema(implementation = PlayerResponseDTO::class))]
        ), ApiResponse(
            responseCode = "404", description = "Player not found", content = [Content()]
        ), ApiResponse(
            responseCode = "403", description = "Forbidden - Admin only", content = [Content()]
        )]
    )
    @PutMapping("/players/{email}/skin")
    fun updatePlayerSkin(
        @PathVariable email: String,
        @Valid @RequestBody updateSkinDTO: UpdateSkinDTO
    ): PlayerResponseDTO {
        return playerService.adminUpdatePlayerSkin(email, updateSkinDTO.skinUrl)
    }

    @Operation(summary = "Update player nickname", description = "Updates a player's nickname (admin only)")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Nickname updated successfully",
            content = [Content(schema = Schema(implementation = PlayerResponseDTO::class))]
        ), ApiResponse(
            responseCode = "404", description = "Player not found", content = [Content()]
        ), ApiResponse(
            responseCode = "403", description = "Forbidden - Admin only", content = [Content()]
        )]
    )
    @PutMapping("/players/{email}/nickname")
    fun updatePlayerNickname(
        @PathVariable email: String,
        @RequestBody @Valid nicknameDto: Map<String, @Valid @Pattern(
            regexp = "^[a-zA-Z0-9_]+$",
            message = "Nickname can only contain letters, numbers and underscores"
        ) @Size(
            min = 3, max = 30, message = "Nickname must be between 3 and 30 characters"
        ) String>
    ): PlayerResponseDTO {
        val nickname = nicknameDto["nickname"] ?: throw ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Nickname is required"
        )
        return playerService.adminUpdateNickname(email, nickname)
    }

    @Operation(summary = "Remove admin role", description = "Removes admin role from a player (admin only)")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Admin role removed successfully",
            content = [Content(schema = Schema(implementation = PlayerResponseDTO::class))]
        ), ApiResponse(
            responseCode = "404", description = "Player not found", content = [Content()]
        ), ApiResponse(
            responseCode = "403", description = "Forbidden - Admin only", content = [Content()]
        )]
    )
    @DeleteMapping("/players/{email}/roles/admin")
    fun removeAdminRole(@PathVariable email: String): PlayerResponseDTO {
        return playerService.removeAdminRole(email)
    }
}