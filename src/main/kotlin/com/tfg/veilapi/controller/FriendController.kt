package com.tfg.veilapi.controller

import com.tfg.veilapi.dto.CreateFriendRequestDTO
import com.tfg.veilapi.dto.FriendRequestDTO
import com.tfg.veilapi.dto.FriendResponseDTO
import com.tfg.veilapi.service.AuthorizationService
import com.tfg.veilapi.service.FriendService
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
@RequestMapping("/api/friends")
@Tag(name = "Friends", description = "Friend relationship management API")
@SecurityRequirement(name = "bearerAuth")
class FriendController(
    private val friendService: FriendService, private val authorizationService: AuthorizationService
) {

    @Operation(summary = "Send friend request", description = "Send a friend request to another player")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "201",
            description = "Friend request sent successfully",
            content = [Content(schema = Schema(implementation = Map::class))]
        ), ApiResponse(
            responseCode = "404", description = "Player not found", content = [Content()]
        ), ApiResponse(
            responseCode = "409", description = "Already friends or request already sent", content = [Content()]
        )]
    )
    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    fun sendFriendRequest(@RequestBody requestDto: CreateFriendRequestDTO): Map<String, Long> {
        val currentUserEmail = authorizationService.getCurrentUserEmail()

        val updatedRequestDto = CreateFriendRequestDTO(
            requesterId = currentUserEmail, playerId = requestDto.playerId
        )
        val requestId = friendService.sendFriendRequest(updatedRequestDto)
        return mapOf("requestId" to requestId)
    }

    @Operation(summary = "Accept friend request", description = "Accept a pending friend request")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Friend request accepted",
            content = [Content(schema = Schema(implementation = FriendResponseDTO::class))]
        ), ApiResponse(
            responseCode = "404", description = "Friend request not found", content = [Content()]
        ), ApiResponse(
            responseCode = "403",
            description = "Forbidden - Cannot accept requests for other users",
            content = [Content()]
        )]
    )
    @PostMapping("/requests/{requestId}/accept")
    fun acceptFriendRequest(@PathVariable requestId: Long): FriendResponseDTO {

        val request = friendService.getFriendRequestById(requestId)

        authorizationService.validateUserAccess(request.playerId)
        return friendService.acceptFriendRequest(requestId)
    }

    @Operation(summary = "Decline friend request", description = "Decline a pending friend request")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "204", description = "Friend request declined successfully", content = [Content()]
        ), ApiResponse(
            responseCode = "404", description = "Friend request not found", content = [Content()]
        ), ApiResponse(
            responseCode = "403",
            description = "Forbidden - Cannot decline requests for other users",
            content = [Content()]
        )]
    )
    @PostMapping("/requests/{requestId}/decline")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun declineFriendRequest(@PathVariable requestId: Long) {

        val request = friendService.getFriendRequestById(requestId)

        authorizationService.validateUserAccess(request.playerId)
        friendService.rejectFriendRequest(requestId)
    }

    @Operation(
        summary = "Get friend requests", description = "Get all pending friend requests for the authenticated user"
    )
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "List of friend requests",
            content = [Content(array = ArraySchema(schema = Schema(implementation = FriendRequestDTO::class)))]
        )]
    )
    @GetMapping("/requests")
    fun getFriendRequests(): List<FriendRequestDTO> {

        val currentUserEmail = authorizationService.getCurrentUserEmail()
        return friendService.getFriendRequests(currentUserEmail)
    }

    @Operation(summary = "Get friends", description = "Get all friends for the authenticated user")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "List of friends",
            content = [Content(array = ArraySchema(schema = Schema(implementation = FriendResponseDTO::class)))]
        )]
    )
    @GetMapping
    fun getFriends(): List<FriendResponseDTO> {

        val currentUserEmail = authorizationService.getCurrentUserEmail()
        return friendService.getFriends(currentUserEmail)
    }

    @Operation(summary = "Remove friend", description = "Remove a friend from the authenticated user's friend list")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "204", description = "Friend removed successfully", content = [Content()]
        ), ApiResponse(
            responseCode = "404", description = "Player not found", content = [Content()]
        )]
    )
    @DeleteMapping("/{friendEmail}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removeFriend(@PathVariable friendEmail: String) {

        val currentUserEmail = authorizationService.getCurrentUserEmail()
        friendService.removeFriend(currentUserEmail, friendEmail)
    }
}
