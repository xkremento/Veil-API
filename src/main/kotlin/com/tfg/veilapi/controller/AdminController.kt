package com.tfg.veilapi.controller

import com.tfg.veilapi.config.ValidationConstants
import com.tfg.veilapi.dto.AddCoinsDTO
import com.tfg.veilapi.dto.PlayerResponseDTO
import com.tfg.veilapi.dto.PlayerUpdateDTO
import com.tfg.veilapi.dto.ProfileImageDTO
import com.tfg.veilapi.dto.UpdateSkinDTO
import com.tfg.veilapi.service.PlayerService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Operations", description = "Administrative operations for managing players")
class AdminController(private val playerService: PlayerService) {

    @GetMapping("/players/{email}")
    @Operation(summary = "Get player details (Admin only)")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Player details retrieved successfully"),
        ApiResponse(responseCode = "403", description = "Admin access required"),
        ApiResponse(responseCode = "404", description = "Player not found")
    ])
    fun getPlayer(@PathVariable email: String): PlayerResponseDTO {
        return playerService.getPlayer(email)
    }

    @PutMapping("/players/{email}")
    @Operation(summary = "Update player details (Admin only)")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Player updated successfully"),
        ApiResponse(responseCode = "400", description = "Invalid player data"),
        ApiResponse(responseCode = "403", description = "Admin access required"),
        ApiResponse(responseCode = "404", description = "Player not found"),
        ApiResponse(responseCode = "409", description = "Nickname already exists")
    ])
    fun updatePlayer(@PathVariable email: String, @Valid @RequestBody updateDto: PlayerUpdateDTO): PlayerResponseDTO {
        return playerService.adminUpdatePlayer(email, updateDto)
    }

    @DeleteMapping("/players/{email}")
    @Operation(summary = "Delete player (Admin only)")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Player deleted successfully"),
        ApiResponse(responseCode = "403", description = "Admin access required"),
        ApiResponse(responseCode = "404", description = "Player not found")
    ])
    fun deletePlayer(@PathVariable email: String): ResponseEntity<Void> {
        playerService.deletePlayer(email)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/players/{email}/coins")
    @Operation(summary = "Add coins to player (Admin only)")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Coins added successfully"),
        ApiResponse(responseCode = "400", description = "Invalid coin amount or would exceed maximum"),
        ApiResponse(responseCode = "403", description = "Admin access required"),
        ApiResponse(responseCode = "404", description = "Player not found")
    ])
    fun addCoinsToPlayer(@PathVariable email: String, @Valid @RequestBody addCoinsDto: AddCoinsDTO): PlayerResponseDTO {
        return playerService.adminAddCoinsToPlayer(email, addCoinsDto.amount)
    }

    @PutMapping("/players/{email}/nickname")
    @Operation(summary = "Update player nickname (Admin only)")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Nickname updated successfully"),
        ApiResponse(responseCode = "400", description = "Invalid nickname format"),
        ApiResponse(responseCode = "403", description = "Admin access required"),
        ApiResponse(responseCode = "404", description = "Player not found"),
        ApiResponse(responseCode = "409", description = "Nickname already exists")
    ])
    fun updatePlayerNickname(
        @PathVariable email: String,
        @RequestBody @Valid request: Map<String, @Valid @Pattern(
            regexp = ValidationConstants.NICKNAME_PATTERN,
            message = ValidationConstants.NICKNAME_MESSAGE
        ) @Size(
            min = ValidationConstants.NICKNAME_MIN_LENGTH,
            max = ValidationConstants.NICKNAME_MAX_LENGTH,
            message = "Nickname must be between ${ValidationConstants.NICKNAME_MIN_LENGTH} and ${ValidationConstants.NICKNAME_MAX_LENGTH} characters"
        ) String>
    ): PlayerResponseDTO {
        val nickname = request["nickname"] ?: throw ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Nickname is required"
        )
        return playerService.adminUpdateNickname(email, nickname)
    }

    @PutMapping("/players/{email}/profile-image")
    @Operation(summary = "Update player profile image (Admin only)")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Profile image updated successfully"),
        ApiResponse(responseCode = "400", description = "Invalid URL format"),
        ApiResponse(responseCode = "403", description = "Admin access required"),
        ApiResponse(responseCode = "404", description = "Player not found")
    ])
    fun updatePlayerProfileImage(@PathVariable email: String, @Valid @RequestBody profileImageDto: ProfileImageDTO): PlayerResponseDTO {
        return playerService.adminUpdatePlayerProfileImage(email, profileImageDto.profileImageUrl)
    }

    @PutMapping("/players/{email}/skin")
    @Operation(summary = "Update player skin (Admin only)")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Skin updated successfully"),
        ApiResponse(responseCode = "400", description = "Invalid URL format"),
        ApiResponse(responseCode = "403", description = "Admin access required"),
        ApiResponse(responseCode = "404", description = "Player not found")
    ])
    fun updatePlayerSkin(@PathVariable email: String, @Valid @RequestBody updateSkinDto: UpdateSkinDTO): PlayerResponseDTO {
        return playerService.adminUpdatePlayerSkin(email, updateSkinDto.skinUrl)
    }

    @PostMapping("/players/{email}/admin-role")
    @Operation(summary = "Grant admin role to player (Admin only)")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Admin role granted successfully"),
        ApiResponse(responseCode = "403", description = "Admin access required"),
        ApiResponse(responseCode = "404", description = "Player not found")
    ])
    fun grantAdminRole(@PathVariable email: String): PlayerResponseDTO {
        return playerService.addAdminRole(email)
    }

    @DeleteMapping("/players/{email}/admin-role")
    @Operation(summary = "Revoke admin role from player (Admin only)")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Admin role revoked successfully"),
        ApiResponse(responseCode = "403", description = "Admin access required"),
        ApiResponse(responseCode = "404", description = "Player not found")
    ])
    fun revokeAdminRole(@PathVariable email: String): PlayerResponseDTO {
        return playerService.removeAdminRole(email)
    }
}