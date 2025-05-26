package com.tfg.veilapi.service

import com.tfg.veilapi.dto.CreateFriendRequestDTO
import com.tfg.veilapi.dto.FriendRequestDTO
import com.tfg.veilapi.dto.FriendResponseDTO
import com.tfg.veilapi.model.FriendRequest
import com.tfg.veilapi.model.Friends
import com.tfg.veilapi.repository.FriendRequestRepository
import com.tfg.veilapi.repository.FriendsRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
@Transactional(readOnly = true)
class FriendService(
    private val friendRequestRepository: FriendRequestRepository,
    private val friendsRepository: FriendsRepository,
    private val playerService: PlayerService,
    private val authorizationService: AuthorizationService
) {

    fun getFriendRequestById(requestId: Long): FriendRequestDTO {
        val request = friendRequestRepository.findById(requestId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Friend request not found") }

        return FriendRequestDTO(
            friendRequestId = request.id,
            requesterId = request.requester.email,
            requesterNickname = request.requester.nickname,
            playerId = request.player.email,
            requesterProfileImageUrl = request.requester.profileImageUrl
        )
    }

    @Transactional
    fun sendFriendRequest(requestDto: CreateFriendRequestDTO): Long {
        // Get the authenticated user as the requester
        val requesterEmail = authorizationService.getCurrentUserEmail()
        val requester = playerService.findPlayerByEmail(requesterEmail)
        val player = playerService.findPlayerByEmail(requestDto.playerId)

        // Prevent self friend requests
        if (requester.email == player.email) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot send a friend request to yourself")
        }

        val existingFriendship = friendsRepository.findByPlayerEmailAndFriendEmail(requester.email, player.email)
        if (existingFriendship.isNotEmpty()) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Already friends")
        }

        if (friendRequestRepository.findByRequesterEmailAndPlayerEmail(requester.email, player.email) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Friend request already sent")
        }

        val friendRequest = FriendRequest(
            requester = requester, player = player
        )

        val savedRequest = friendRequestRepository.save(friendRequest)
        return savedRequest.id
    }

    @Transactional
    fun acceptFriendRequest(requestId: Long): FriendResponseDTO {
        val request = friendRequestRepository.findById(requestId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Friend request not found") }

        val now = LocalDateTime.now()
        val friendship1 = Friends(
            player = request.player, friend = request.requester, friendshipDateTime = now
        )

        val friendship2 = Friends(
            player = request.requester, friend = request.player, friendshipDateTime = now
        )

        request.player.friends.add(request.requester)
        request.requester.friends.add(request.player)

        friendsRepository.save(friendship1)
        friendsRepository.save(friendship2)

        friendRequestRepository.delete(request)

        return FriendResponseDTO(
            email = request.requester.email,
            nickname = request.requester.nickname,
            friendshipDate = now.format(DateTimeFormatter.ISO_DATE_TIME),
            profileImageUrl = request.requester.profileImageUrl,
            skinUrl = request.requester.skinUrl
        )
    }

    @Transactional
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
                friendRequestId = it.id,
                requesterId = it.requester.email,
                requesterNickname = it.requester.nickname,
                playerId = it.player.email,
                requesterProfileImageUrl = it.requester.profileImageUrl
            )
        }
    }

    fun getFriends(playerEmail: String): List<FriendResponseDTO> {
        val player = playerService.findPlayerByEmail(playerEmail)

        return player.friends.map { friend ->
            val friendships = friendsRepository.findByPlayerEmailAndFriendEmail(player.email, friend.email)

            if (friendships.isEmpty()) {
                throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Friendship not found")
            }

            val friendship = friendships.firstOrNull() ?: throw ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Friendship not found"
            )

            FriendResponseDTO(
                email = friend.email,
                nickname = friend.nickname,
                friendshipDate = friendship.friendshipDateTime.format(DateTimeFormatter.ISO_DATE_TIME),
                profileImageUrl = friend.profileImageUrl,
                skinUrl = friend.skinUrl
            )
        }
    }

    @Transactional
    fun removeFriend(playerEmail: String, friendEmail: String) {
        val player = playerService.findPlayerByEmail(playerEmail)
        val friend = playerService.findPlayerByEmail(friendEmail)

        player.friends.remove(friend)
        friend.friends.remove(player)

        val friendships1 = friendsRepository.findByPlayerEmailAndFriendEmail(player.email, friend.email)
        val friendships2 = friendsRepository.findByPlayerEmailAndFriendEmail(friend.email, player.email)

        friendships1.forEach { friendsRepository.delete(it) }
        friendships2.forEach { friendsRepository.delete(it) }
    }
}