package com.tfg.veilapi.config

import com.tfg.veilapi.model.FriendRequest
import com.tfg.veilapi.model.Friends
import com.tfg.veilapi.model.Game
import com.tfg.veilapi.model.Player
import com.tfg.veilapi.model.PlayerGame
import com.tfg.veilapi.repository.FriendRequestRepository
import com.tfg.veilapi.repository.FriendsRepository
import com.tfg.veilapi.repository.GameRepository
import com.tfg.veilapi.repository.PlayerGameRepository
import com.tfg.veilapi.repository.PlayerRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class LoadExampleData(
    private val playerRepository: PlayerRepository,
    private val friendsRepository: FriendsRepository,
    private val friendRequestRepository: FriendRequestRepository,
    private val gameRepository: GameRepository,
    private val playerGameRepository: PlayerGameRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        // Clear any existing data
        playerGameRepository.deleteAll()
        friendRequestRepository.deleteAll()
        friendsRepository.deleteAll()
        gameRepository.deleteAll()
        playerRepository.deleteAll()

        // Create sample players
        val player1 = Player(
            email = "john@example.com",
            nickname = "JohnDoe",
            password = passwordEncoder.encode("password123"),
            coins = 100,
            skinUrl = "https://example.com/skins/default.png"
        )

        val player2 = Player(
            email = "sarah@example.com",
            nickname = "SarahGamer",
            password = passwordEncoder.encode("password456"),
            coins = 150,
            skinUrl = "https://example.com/skins/blue.png"
        )

        val player3 = Player(
            email = "mike@example.com",
            nickname = "MikeTheMurderer",
            password = passwordEncoder.encode("password789"),
            coins = 200,
            skinUrl = "https://example.com/skins/red.png"
        )

        val player4 = Player(
            email = "alex@example.com",
            nickname = "AlexTheVictim",
            password = passwordEncoder.encode("password321"),
            coins = 50,
            skinUrl = "https://example.com/skins/green.png"
        )

        val player5 = Player(
            email = "emma@example.com",
            nickname = "EmmaPlays",
            password = passwordEncoder.encode("password654"),
            coins = 75,
            skinUrl = "https://example.com/skins/purple.png"
        )

        // Save all players
        val savedPlayer1 = playerRepository.save(player1)
        val savedPlayer2 = playerRepository.save(player2)
        val savedPlayer3 = playerRepository.save(player3)
        val savedPlayer4 = playerRepository.save(player4)
        val savedPlayer5 = playerRepository.save(player5)

        // Create friendships (bidirectional)
        // John and Sarah are friends
        val friendship1 = Friends(
            player = savedPlayer1, friend = savedPlayer2, friendshipDateTime = LocalDateTime.now().minusDays(30)
        )
        val friendship2 = Friends(
            player = savedPlayer2, friend = savedPlayer1, friendshipDateTime = LocalDateTime.now().minusDays(30)
        )

        // John and Mike are friends
        val friendship3 = Friends(
            player = savedPlayer1, friend = savedPlayer3, friendshipDateTime = LocalDateTime.now().minusDays(15)
        )
        val friendship4 = Friends(
            player = savedPlayer3, friend = savedPlayer1, friendshipDateTime = LocalDateTime.now().minusDays(15)
        )

        // Sarah and Mike are friends
        val friendship5 = Friends(
            player = savedPlayer2, friend = savedPlayer3, friendshipDateTime = LocalDateTime.now().minusDays(7)
        )
        val friendship6 = Friends(
            player = savedPlayer3, friend = savedPlayer2, friendshipDateTime = LocalDateTime.now().minusDays(7)
        )

        // Save friendships
        friendsRepository.saveAll(listOf(friendship1, friendship2, friendship3, friendship4, friendship5, friendship6))

        // Update player friend collections to reflect these friendships
        savedPlayer1.friends.add(savedPlayer2)
        savedPlayer1.friends.add(savedPlayer3)
        savedPlayer2.friends.add(savedPlayer1)
        savedPlayer2.friends.add(savedPlayer3)
        savedPlayer3.friends.add(savedPlayer1)
        savedPlayer3.friends.add(savedPlayer2)

        playerRepository.saveAll(listOf(savedPlayer1, savedPlayer2, savedPlayer3))

        // Create pending friend requests
        // Alex sent a request to John
        val friendRequest1 = FriendRequest(
            requester = savedPlayer4, player = savedPlayer1
        )

        // Emma sent a request to John
        val friendRequest2 = FriendRequest(
            requester = savedPlayer5, player = savedPlayer1
        )

        // Emma sent a request to Sarah
        val friendRequest3 = FriendRequest(
            requester = savedPlayer5, player = savedPlayer2
        )

        val friendRequest4 = FriendRequest(
            requester = savedPlayer1, player = savedPlayer2
        )

        // Save friend requests
        friendRequestRepository.saveAll(listOf(friendRequest1, friendRequest2, friendRequest3, friendRequest4))

        // Create games
        val game1 = Game(
            duration = 300 // 5 minutes
        )

        val game2 = Game(
            duration = 600 // 10 minutes
        )

        // Save games
        val savedGame1 = gameRepository.save(game1)
        val savedGame2 = gameRepository.save(game2)

        // Create player-game relationships
        // Game 1 with John, Sarah, and Mike (Mike is the murderer)
        val playerGame1 = PlayerGame(
            player = savedPlayer1,
            game = savedGame1,
            playerIsMurderer = false,
            gameDateTime = LocalDateTime.now().minusDays(5)
        )

        val playerGame2 = PlayerGame(
            player = savedPlayer2,
            game = savedGame1,
            playerIsMurderer = false,
            gameDateTime = LocalDateTime.now().minusDays(5)
        )

        val playerGame3 = PlayerGame(
            player = savedPlayer3,
            game = savedGame1,
            playerIsMurderer = true,
            gameDateTime = LocalDateTime.now().minusDays(5)
        )

        // Game 2 with all players (Alex is the murderer)
        val playerGame4 = PlayerGame(
            player = savedPlayer1,
            game = savedGame2,
            playerIsMurderer = false,
            gameDateTime = LocalDateTime.now().minusDays(1)
        )

        val playerGame5 = PlayerGame(
            player = savedPlayer2,
            game = savedGame2,
            playerIsMurderer = false,
            gameDateTime = LocalDateTime.now().minusDays(1)
        )

        val playerGame6 = PlayerGame(
            player = savedPlayer3,
            game = savedGame2,
            playerIsMurderer = false,
            gameDateTime = LocalDateTime.now().minusDays(1)
        )

        val playerGame7 = PlayerGame(
            player = savedPlayer4,
            game = savedGame2,
            playerIsMurderer = true,
            gameDateTime = LocalDateTime.now().minusDays(1)
        )

        val playerGame8 = PlayerGame(
            player = savedPlayer5,
            game = savedGame2,
            playerIsMurderer = false,
            gameDateTime = LocalDateTime.now().minusDays(1)
        )

        // Save player-game relationships
        playerGameRepository.saveAll(
            listOf(
                playerGame1, playerGame2, playerGame3, playerGame4, playerGame5, playerGame6, playerGame7, playerGame8
            )
        )

        println("Sample data has been initialized!")
    }
}