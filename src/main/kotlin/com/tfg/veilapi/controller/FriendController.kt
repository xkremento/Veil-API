package com.tfg.veilapi.controller

import com.tfg.veilapi.dto.FriendRequestDTO
import com.tfg.veilapi.dto.FriendResponseDTO
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
class FriendController(private val friendService: FriendService) {

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
    fun sendFriendRequest(@RequestBody requestDto: FriendRequestDTO): Map<String, Long> {
        val requestId = friendService.sendFriendRequest(requestDto)
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
        )]
    )

    @PostMapping("/requests/{requestId}/accept")
    fun acceptFriendRequest(@PathVariable requestId: Long): FriendResponseDTO {
        return friendService.acceptFriendRequest(requestId)
    }

    @Operation(summary = "Reject friend request", description = "Reject a pending friend request")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "204", description = "Friend request rejected successfully", content = [Content()]
        ), ApiResponse(
            responseCode = "404", description = "Friend request not found", content = [Content()]
        )]
    )

    @DeleteMapping("/requests/{requestId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun rejectFriendRequest(@PathVariable requestId: Long) {
        friendService.rejectFriendRequest(requestId)
    }

    @Operation(summary = "Get friend requests", description = "Get all pending friend requests for a player")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "List of friend requests",
            content = [Content(array = ArraySchema(schema = Schema(implementation = FriendRequestDTO::class)))]
        ), ApiResponse(
            responseCode = "404", description = "Player not found", content = [Content()]
        )]
    )

    @GetMapping("/requests/{playerEmail}")
    fun getFriendRequests(@PathVariable playerEmail: String): List<FriendRequestDTO> {
        return friendService.getFriendRequests(playerEmail)
    }

    @Operation(summary = "Get friends", description = "Get all friends for a player")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "List of friends",
            content = [Content(array = ArraySchema(schema = Schema(implementation = FriendResponseDTO::class)))]
        ), ApiResponse(
            responseCode = "404", description = "Player not found", content = [Content()]
        )]
    )

    @GetMapping("/{playerEmail}")
    fun getFriends(@PathVariable playerEmail: String): List<FriendResponseDTO> {
        return friendService.getFriends(playerEmail)
    }

    @Operation(summary = "Remove friend", description = "Remove a friend from a player's friend list")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "204", description = "Friend removed successfully", content = [Content()]
        ), ApiResponse(
            responseCode = "404", description = "Player not found", content = [Content()]
        )]
    )

    @DeleteMapping("/{playerEmail}/{friendEmail}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removeFriend(
        @PathVariable playerEmail: String, @PathVariable friendEmail: String
    ) {
        friendService.removeFriend(playerEmail, friendEmail)
    }
}