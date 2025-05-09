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
                email = adminEmail, nickname = adminNickname, password = passwordEncoder.encode(adminPassword)
            )

            playerRepository.save(adminPlayer)
            // Assign admin role
            playerService.addAdminRole(adminEmail)

            println("Admin user created: $adminEmail")
        }
    }

    /**
     * Creates players, friendships, friend requests, and games
     */
    private fun loadSampleData() {
        try {
            // Clear existing data
            entityManager.createNativeQuery("DELETE FROM player_game").executeUpdate()
            entityManager.createNativeQuery("DELETE FROM friend_request").executeUpdate()
            entityManager.createNativeQuery("DELETE FROM friends").executeUpdate()
            entityManager.createNativeQuery("DELETE FROM game").executeUpdate()

            // Don't delete admin user
            entityManager.createNativeQuery("DELETE FROM player_roles WHERE player_email != :adminEmail")
                .setParameter("adminEmail", adminEmail).executeUpdate()

            entityManager.createNativeQuery("DELETE FROM player WHERE email != :adminEmail")
                .setParameter("adminEmail", adminEmail).executeUpdate()

            entityManager.flush()

            // Create sample players
            val player1 = Player(
                email = "paco@veil.com",
                nickname = "DonCacaolate",
                password = passwordEncoder.encode("password123"),
                coins = 100,
                skinUrl = "https://example.com/skins/default.png",
                profileImageUrl = "https://example.com/profiles/paco.png"
            )

            val player2 = Player(
                email = "manolo@veil.com",
                nickname = "SalsichaGalactica",
                password = passwordEncoder.encode("password456"),
                coins = 150,
                skinUrl = "https://example.com/skins/blue.png",
                profileImageUrl = "https://example.com/profiles/manolo.png"
            )

            val player3 = Player(
                email = "pepe@veil.com",
                nickname = "MatarileRilero",
                password = passwordEncoder.encode("password789"),
                coins = 200,
                skinUrl = "https://example.com/skins/red.png",
                profileImageUrl = "https://example.com/profiles/pepe.png"
            )

            val player4 = Player(
                email = "antonio@veil.com",
                nickname = "PanicoPajarito",
                password = passwordEncoder.encode("password321"),
                coins = 50,
                skinUrl = "https://example.com/skins/green.png",
                profileImageUrl = "https://example.com/profiles/antonio.png"
            )

            val player5 = Player(
                email = "juanito@veil.com",
                nickname = "ChicleExplotaglobos",
                password = passwordEncoder.encode("password654"),
                coins = 75,
                skinUrl = "https://example.com/skins/purple.png",
                profileImageUrl = "https://example.com/profiles/juanito.png"
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
            // Paco and Manolo are friends
            val friendship1 = Friends(
                player = savedPlayer1, friend = savedPlayer2, friendshipDateTime = LocalDateTime.now().minusDays(30)
            )
            val friendship2 = Friends(
                player = savedPlayer2, friend = savedPlayer1, friendshipDateTime = LocalDateTime.now().minusDays(30)
            )

            // Paco and Pepe are friends
            val friendship3 = Friends(
                player = savedPlayer1, friend = savedPlayer3, friendshipDateTime = LocalDateTime.now().minusDays(15)
            )
            val friendship4 = Friends(
                player = savedPlayer3, friend = savedPlayer1, friendshipDateTime = LocalDateTime.now().minusDays(15)
            )

            // Manolo and Pepe are friends
            val friendship5 = Friends(
                player = savedPlayer2, friend = savedPlayer3, friendshipDateTime = LocalDateTime.now().minusDays(7)
            )
            val friendship6 = Friends(
                player = savedPlayer3, friend = savedPlayer2, friendshipDateTime = LocalDateTime.now().minusDays(7)
            )

            // Save friendships
            friendsRepository.saveAll(
                listOf(
                    friendship1, friendship2, friendship3, friendship4, friendship5, friendship6
                )
            )

            // Flush to ensure friendships are saved
            entityManager.flush()

            // Create pending friend requests
            // Antonio sent a request to Paco
            val friendRequest1 = FriendRequest(
                requester = savedPlayer4, player = savedPlayer1
            )

            // Juanito sent a request to Paco
            val friendRequest2 = FriendRequest(
                requester = savedPlayer5, player = savedPlayer1
            )

            // Juanito sent a request to Manolo
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
            // Game 1 with Paco, Manolo, and Pepe (Pepe is the murderer)
            val playerGame1 = PlayerGame(
                player = savedPlayer1,
                game = savedGame1,
                role = GameRole.INNOCENT,
                gameDateTime = LocalDateTime.now().minusDays(5)
            )

            val playerGame2 = PlayerGame(
                player = savedPlayer2,
                game = savedGame1,
                role = GameRole.INNOCENT,
                gameDateTime = LocalDateTime.now().minusDays(5)
            )

            val playerGame3 = PlayerGame(
                player = savedPlayer3,
                game = savedGame1,
                role = GameRole.MURDERER,
                gameDateTime = LocalDateTime.now().minusDays(5)
            )

            // Game 2 with all players (Antonio is the murderer)
            val playerGame4 = PlayerGame(
                player = savedPlayer1,
                game = savedGame2,
                role = GameRole.INNOCENT,
                gameDateTime = LocalDateTime.now().minusDays(1)
            )

            val playerGame5 = PlayerGame(
                player = savedPlayer2,
                game = savedGame2,
                role = GameRole.INNOCENT,
                gameDateTime = LocalDateTime.now().minusDays(1)
            )

            val playerGame6 = PlayerGame(
                player = savedPlayer3,
                game = savedGame2,
                role = GameRole.INNOCENT,
                gameDateTime = LocalDateTime.now().minusDays(1)
            )

            val playerGame7 = PlayerGame(
                player = savedPlayer4,
                game = savedGame2,
                role = GameRole.MURDERER,
                gameDateTime = LocalDateTime.now().minusDays(1)
            )

            val playerGame8 = PlayerGame(
                player = savedPlayer5,
                game = savedGame2,
                role = GameRole.INNOCENT,
                gameDateTime = LocalDateTime.now().minusDays(1)
            )

            // Save player-game relationships
            playerGameRepository.saveAll(
                listOf(
                    playerGame1,
                    playerGame2,
                    playerGame3,
                    playerGame4,
                    playerGame5,
                    playerGame6,
                    playerGame7,
                    playerGame8
                )
            )

            println("Sample data has been initialized!")
        } catch (e: Exception) {
            println("Error loading sample data: ${e.message}")
            e.printStackTrace()
        }
    }
}