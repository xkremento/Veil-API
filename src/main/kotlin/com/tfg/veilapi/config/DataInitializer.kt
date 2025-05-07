package com.tfg.veilapi.config

import com.tfg.veilapi.model.*
import com.tfg.veilapi.repository.*
import com.tfg.veilapi.service.PlayerService
import com.tfg.veilapi.service.RoleService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext

@Component
class DataInitializer(
    private val playerRepository: PlayerRepository,
    private val friendsRepository: FriendsRepository,
    private val friendRequestRepository: FriendRequestRepository,
    private val gameRepository: GameRepository,
    private val playerGameRepository: PlayerGameRepository,
    private val roleService: RoleService,
    private val playerService: PlayerService,
    private val passwordEncoder: PasswordEncoder,
    @Value("\${app.admin.email:admin@veil.com}") private val adminEmail: String,
    @Value("\${app.admin.password:adminPassword}") private val adminPassword: String,
    @Value("\${app.admin.nickname:admin}") private val adminNickname: String,
    @Value("\${app.data.load-sample:true}") private val loadSampleData: Boolean
) : CommandLineRunner {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Transactional
    override fun run(vararg args: String?) {
        // Create admin user if it doesn't exist
        createAdminUser()

        // Load sample data if enabled in configuration
        if (loadSampleData) {
            loadSampleData()
        }
    }

    /**
     * Create an admin user if it doesn't already exist in the database
     */
    private fun createAdminUser() {
        if (!playerRepository.existsById(adminEmail)) {
            val adminPlayer = Player(
                email = adminEmail,
                nickname = adminNickname,
                password = passwordEncoder.encode(adminPassword)
            )

            playerRepository.save(adminPlayer)
            // Assign admin role
            playerService.addAdminRole(adminEmail)

            println("Admin user created: $adminEmail")
        }
    }

    /**
     * Load sample data for development and testing purposes
     * Creates players, friendships, friend requests, and games
     */
    private fun loadSampleData() {
        // The safer way to clear all data is with native queries that bypass Hibernate's caching and collections
        try {
            // Clear existing data using native queries to avoid cascading issues
            entityManager.createNativeQuery("DELETE FROM player_game").executeUpdate()
            entityManager.createNativeQuery("DELETE FROM friend_request").executeUpdate()
            entityManager.createNativeQuery("DELETE FROM friends").executeUpdate()
            entityManager.createNativeQuery("DELETE FROM game").executeUpdate()

            // Don't delete admin user
            entityManager.createNativeQuery("DELETE FROM player_roles WHERE player_email != :adminEmail")
                .setParameter("adminEmail", adminEmail)
                .executeUpdate()

            entityManager.createNativeQuery("DELETE FROM player WHERE email != :adminEmail")
                .setParameter("adminEmail", adminEmail)
                .executeUpdate()

            entityManager.flush()

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

            // Add USER role to all players
            player1.roles.add(roleService.findByName("USER"))
            player2.roles.add(roleService.findByName("USER"))
            player3.roles.add(roleService.findByName("USER"))
            player4.roles.add(roleService.findByName("USER"))
            player5.roles.add(roleService.findByName("USER"))

            // Save all players
            val savedPlayer1 = playerRepository.save(player1)
            val savedPlayer2 = playerRepository.save(player2)
            val savedPlayer3 = playerRepository.save(player3)
            val savedPlayer4 = playerRepository.save(player4)
            val savedPlayer5 = playerRepository.save(player5)

            // Flush to ensure players are saved before creating relationships
            entityManager.flush()

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

            // Flush to ensure friendships are saved
            entityManager.flush()

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

            // Flush to ensure friend requests are saved
            entityManager.flush()

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

            // Flush to ensure games are saved
            entityManager.flush()

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
        } catch (e: Exception) {
            println("Error loading sample data: ${e.message}")
            e.printStackTrace()
        }
    }
}