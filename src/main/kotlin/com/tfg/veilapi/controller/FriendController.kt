package com.tfg.veilapi.controller

import com.tfg.veilapi.dto.CreateFriendRequestDTO
import com.tfg.veilapi.dto.FriendRequestDTO
import com.tfg.veilapi.dto.FriendResponseDTO
import com.tfg.veilapi.service.AuthorizationService
import com.tfg.veilapi.service.FriendService
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
@RequestMapping("/friends")
@Tag(name = "Friend Management", description = "Operations for managing friend relationships")
class FriendController(
    private val friendService: FriendService,
    private val authorizationService: AuthorizationService
) {

    @PostMapping("/requests")
    @Operation(summary = "Send a friend request")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Friend request sent successfully"),
        ApiResponse(responseCode = "400", description = "Invalid request data or cannot send request to yourself"),
        ApiResponse(responseCode = "409", description = "Friend request already sent or already friends")
    ])
    fun sendFriendRequest(@Valid @RequestBody requestDto: CreateFriendRequestDTO): ResponseEntity<Map<String, Any>> {
        //Verify that you don't send a request to yourself
        if (requestDto.requesterId == requestDto.playerId) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot send friend request to yourself")
        }

        //Verify that the authenticated user is the sender
        authorizationService.validateUserAccess(requestDto.requesterId)

        val requestId = friendService.sendFriendRequest(requestDto)
        val response = mapOf(
            "message" to "Friend request sent successfully",
            "friendRequestId" to requestId
        )
        return ResponseEntity(response, HttpStatus.CREATED)
    }

    @PostMapping("/requests/{requestId}/accept")
    @Operation(summary = "Accept a friend request")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Friend request accepted successfully"),
        ApiResponse(responseCode = "403", description = "Not authorized to accept this request"),
        ApiResponse(responseCode = "404", description = "Friend request not found")
    ])
    fun acceptFriendRequest(@PathVariable requestId: Long): ResponseEntity<FriendResponseDTO> {
        //Verify that the authenticated user is the recipient of the request
        val friendRequest = friendService.getFriendRequestById(requestId)
        authorizationService.validateUserAccess(friendRequest.playerId)

        val newFriend = friendService.acceptFriendRequest(requestId)
        return ResponseEntity.ok(newFriend)
    }

    @DeleteMapping("/requests/{requestId}")
    @Operation(summary = "Reject or cancel a friend request")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Friend request rejected/cancelled successfully"),
        ApiResponse(responseCode = "403", description = "Not authorized to modify this request"),
        ApiResponse(responseCode = "404", description = "Friend request not found")
    ])
    fun rejectFriendRequest(@PathVariable requestId: Long): ResponseEntity<Void> {
        //Verify that the authenticated user is the recipient or sender
        val friendRequest = friendService.getFriendRequestById(requestId)
        val currentUserEmail = authorizationService.getCurrentUserEmail()

        if (currentUserEmail != friendRequest.playerId && currentUserEmail != friendRequest.requesterId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to modify this friend request")
        }

        friendService.rejectFriendRequest(requestId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/requests")
    @Operation(summary = "Get incoming friend requests for authenticated user")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Friend requests retrieved successfully")
    ])
    fun getFriendRequests(): ResponseEntity<List<FriendRequestDTO>> {
        val currentUserEmail = authorizationService.getCurrentUserEmail()
        val requests = friendService.getFriendRequests(currentUserEmail)
        return ResponseEntity.ok(requests)
    }

    @GetMapping
    @Operation(summary = "Get friends list for authenticated user")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Friends list retrieved successfully")
    ])
    fun getFriends(): ResponseEntity<List<FriendResponseDTO>> {
        val currentUserEmail = authorizationService.getCurrentUserEmail()
        val friends = friendService.getFriends(currentUserEmail)
        return ResponseEntity.ok(friends)
    }

    @DeleteMapping("/{friendEmail}")
    @Operation(summary = "Remove a friend")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Friend removed successfully"),
        ApiResponse(responseCode = "404", description = "Friend not found")
    ])
    fun removeFriend(@PathVariable friendEmail: String): ResponseEntity<Void> {
        val currentUserEmail = authorizationService.getCurrentUserEmail()
        friendService.removeFriend(currentUserEmail, friendEmail)
        return ResponseEntity.noContent().build()
    }
}