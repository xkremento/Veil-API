package com.tfg.veilapi.service

import com.tfg.veilapi.dto.FriendRequestDTO
import com.tfg.veilapi.dto.FriendResponseDTO
import com.tfg.veilapi.model.FriendRequest
import com.tfg.veilapi.model.Friends
import com.tfg.veilapi.repository.FriendRequestRepository
import com.tfg.veilapi.repository.FriendsRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class FriendService(
    private val friendRequestRepository: FriendRequestRepository,
    private val friendsRepository: FriendsRepository,
    private val playerService: PlayerService
) {

    fun getFriendRequestById(requestId: Long): FriendRequestDTO {
        val request = friendRequestRepository.findById(requestId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Friend request not found") }

        return FriendRequestDTO(
            friendRequestId = request.id, requesterId = request.requester.email, playerId = request.player.email
        )
    }

    fun sendFriendRequest(requestDto: FriendRequestDTO): Long {
        val requester = playerService.findPlayerByEmail(requestDto.requesterId)
        val player = playerService.findPlayerByEmail(requestDto.playerId)

        // Check if they're already friends
        if (friendsRepository.findByPlayerEmailAndFriendEmail(requester.email, player.email) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Already friends")
        }

        // Check if request already exists
        if (friendRequestRepository.findByRequesterEmailAndPlayerEmail(requester.email, player.email) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Friend request already sent")
        }

        val friendRequest = FriendRequest(
            requester = requester, player = player
        )

        val savedRequest = friendRequestRepository.save(friendRequest)
        return savedRequest.id
    }

    fun acceptFriendRequest(requestId: Long): FriendResponseDTO {
        val request = friendRequestRepository.findById(requestId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Friend request not found") }

        // Create friendship relations (both ways)
        val friendship1 = Friends(
            player = request.player, friend = request.requester, friendshipDateTime = LocalDateTime.now()
        )

        val friendship2 = Friends(
            player = request.requester, friend = request.player, friendshipDateTime = LocalDateTime.now()
        )

        // Update players' friend lists
        request.player.friends.add(request.requester)
        request.requester.friends.add(request.player)

        // Save friendships
        friendsRepository.save(friendship1)
        friendsRepository.save(friendship2)

        // Delete request
        friendRequestRepository.delete(request)

        return FriendResponseDTO(
            email = request.requester.email,
            nickname = request.requester.nickname,
            friendshipDate = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        )
    }

    fun rejectFriendRequest(requestId: Long) {
        if (!friendRequestRepository.existsById(requestId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Friend request not found")
        }
        friendRequestRepository.deleteById(requestId)
    }

    fun getFriendRequests(playerEmail: String): List<FriendRequestDTO> {
        val player = playerService.findPlayerByEmail(playerEmail)
        val requests = friendRequestRepository.findByPlayerEmail(player.email)

        return requests.map {
            FriendRequestDTO(
                friendRequestId = it.id, requesterId = it.requester.email, playerId = it.player.email
            )
        }
    }

    fun getFriends(playerEmail: String): List<FriendResponseDTO> {
        val player = playerService.findPlayerByEmail(playerEmail)

        return player.friends.map { friend ->
            val friendship = friendsRepository.findByPlayerEmailAndFriendEmail(player.email, friend.email)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Friendship not found")

            FriendResponseDTO(
                email = friend.email,
                nickname = friend.nickname,
                friendshipDate = friendship.friendshipDateTime.format(DateTimeFormatter.ISO_DATE_TIME)
            )
        }
    }

    fun removeFriend(playerEmail: String, friendEmail: String) {
        val player = playerService.findPlayerByEmail(playerEmail)
        val friend = playerService.findPlayerByEmail(friendEmail)

        // Remove from both players' friend lists
        player.friends.remove(friend)
        friend.friends.remove(player)

        // Delete friendship records
        val friendship1 = friendsRepository.findByPlayerEmailAndFriendEmail(player.email, friend.email)
        val friendship2 = friendsRepository.findByPlayerEmailAndFriendEmail(friend.email, player.email)

        if (friendship1 != null) friendsRepository.delete(friendship1)
        if (friendship2 != null) friendsRepository.delete(friendship2)
    }
}