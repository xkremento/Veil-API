package com.tfg.veilapi.config

import com.tfg.veilapi.model.*
import com.tfg.veilapi.repository.*
import com.tfg.veilapi.service.PlayerService
import com.tfg.veilapi.service.RoleService
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

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
     * Creates players, friendships, friend requests, and games with humorous Spanish usernames
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

            // Create sample players with humorous Spanish nicknames
            val player1 = Player(
                email = "paquito@veil.com",
                nickname = "PakitoChokolatero",
                password = passwordEncoder.encode("password123"),
                coins = 150,
                skinUrl = "https://example.com/skins/chocolate.png",
                profileImageUrl = "https://example.com/profiles/paquito.png"
            )

            val player2 = Player(
                email = "manolo@veil.com",
                nickname = "ManolitoElCabesa",
                password = passwordEncoder.encode("password456"),
                coins = 250,
                skinUrl = "https://example.com/skins/cabeza.png",
                profileImageUrl = "https://example.com/profiles/manolo.png"
            )

            val player3 = Player(
                email = "pepito@veil.com",
                nickname = "PepitoGrilloLoco",
                password = passwordEncoder.encode("password789"),
                coins = 300,
                skinUrl = "https://example.com/skins/grillo.png",
                profileImageUrl = "https://example.com/profiles/pepito.png"
            )

            val player4 = Player(
                email = "juanito@veil.com",
                nickname = "JuanitoAlimaña",
                password = passwordEncoder.encode("password321"),
                coins = 120,
                skinUrl = "https://example.com/skins/alimaña.png",
                profileImageUrl = "https://example.com/profiles/juanito.png"
            )

            val player5 = Player(
                email = "lolito@veil.com",
                nickname = "LolitoFdezGaming",
                password = passwordEncoder.encode("password654"),
                coins = 450,
                skinUrl = "https://example.com/skins/gamer.png",
                profileImageUrl = "https://example.com/profiles/lolito.png"
            )

            val player6 = Player(
                email = "carmela@veil.com",
                nickname = "CarmelaLaChoni",
                password = passwordEncoder.encode("password987"),
                coins = 200,
                skinUrl = "https://example.com/skins/choni.png",
                profileImageUrl = "https://example.com/profiles/carmela.png"
            )

            val player7 = Player(
                email = "mariano@veil.com",
                nickname = "MarianoRajoyB",
                password = passwordEncoder.encode("password741"),
                coins = 180,
                skinUrl = "https://example.com/skins/politico.png",
                profileImageUrl = "https://example.com/profiles/mariano.png"
            )

            val player8 = Player(
                email = "fede@veil.com",
                nickname = "FedeToroSalvaje",
                password = passwordEncoder.encode("password852"),
                coins = 350,
                skinUrl = "https://example.com/skins/toro.png",
                profileImageUrl = "https://example.com/profiles/fede.png"
            )

            val player9 = Player(
                email = "concha@veil.com",
                nickname = "ConchaVelascOh",
                password = passwordEncoder.encode("password963"),
                coins = 275,
                skinUrl = "https://example.com/skins/actress.png",
                profileImageUrl = "https://example.com/profiles/concha.png"
            )

            val player10 = Player(
                email = "trini@veil.com",
                nickname = "TriniLaBorracha",
                password = passwordEncoder.encode("password159"),
                coins = 95,
                skinUrl = "https://example.com/skins/fiesta.png",
                profileImageUrl = "https://example.com/profiles/trini.png"
            )

            // Add USER role to all players
            player1.roles.add(roleService.findByName("USER"))
            player2.roles.add(roleService.findByName("USER"))
            player3.roles.add(roleService.findByName("USER"))
            player4.roles.add(roleService.findByName("USER"))
            player5.roles.add(roleService.findByName("USER"))
            player6.roles.add(roleService.findByName("USER"))
            player7.roles.add(roleService.findByName("USER"))
            player8.roles.add(roleService.findByName("USER"))
            player9.roles.add(roleService.findByName("USER"))
            player10.roles.add(roleService.findByName("USER"))

            // Make one player admin for testing
            player5.roles.add(roleService.findByName("ADMIN"))

            // Save all players
            val savedPlayer1 = playerRepository.save(player1)
            val savedPlayer2 = playerRepository.save(player2)
            val savedPlayer3 = playerRepository.save(player3)
            val savedPlayer4 = playerRepository.save(player4)
            val savedPlayer5 = playerRepository.save(player5)
            val savedPlayer6 = playerRepository.save(player6)
            val savedPlayer7 = playerRepository.save(player7)
            val savedPlayer8 = playerRepository.save(player8)
            val savedPlayer9 = playerRepository.save(player9)
            val savedPlayer10 = playerRepository.save(player10)

            // Flush to ensure players are saved before creating relationships
            entityManager.flush()

            // Create friendships (bidirectional)
            // PakitoChokolatero and ManolitoElCabesa are friends
            val friendship1 = Friends(
                player = savedPlayer1, friend = savedPlayer2, friendshipDateTime = LocalDateTime.now().minusDays(30)
            )
            val friendship2 = Friends(
                player = savedPlayer2, friend = savedPlayer1, friendshipDateTime = LocalDateTime.now().minusDays(30)
            )

            // PakitoChokolatero and PepitoGrilloLoco are friends
            val friendship3 = Friends(
                player = savedPlayer1, friend = savedPlayer3, friendshipDateTime = LocalDateTime.now().minusDays(15)
            )
            val friendship4 = Friends(
                player = savedPlayer3, friend = savedPlayer1, friendshipDateTime = LocalDateTime.now().minusDays(15)
            )

            // ManolitoElCabesa and PepitoGrilloLoco are friends
            val friendship5 = Friends(
                player = savedPlayer2, friend = savedPlayer3, friendshipDateTime = LocalDateTime.now().minusDays(7)
            )
            val friendship6 = Friends(
                player = savedPlayer3, friend = savedPlayer2, friendshipDateTime = LocalDateTime.now().minusDays(7)
            )

            // PepitoGrilloLoco and JuanitoAlimaña are friends
            val friendship7 = Friends(
                player = savedPlayer3, friend = savedPlayer4, friendshipDateTime = LocalDateTime.now().minusDays(45)
            )
            val friendship8 = Friends(
                player = savedPlayer4, friend = savedPlayer3, friendshipDateTime = LocalDateTime.now().minusDays(45)
            )

            // LolitoFdezGaming and CarmelaLaChoni are friends
            val friendship9 = Friends(
                player = savedPlayer5, friend = savedPlayer6, friendshipDateTime = LocalDateTime.now().minusDays(20)
            )
            val friendship10 = Friends(
                player = savedPlayer6, friend = savedPlayer5, friendshipDateTime = LocalDateTime.now().minusDays(20)
            )

            // ConchaVelascOh and TriniLaBorracha are friends
            val friendship11 = Friends(
                player = savedPlayer9, friend = savedPlayer10, friendshipDateTime = LocalDateTime.now().minusDays(60)
            )
            val friendship12 = Friends(
                player = savedPlayer10, friend = savedPlayer9, friendshipDateTime = LocalDateTime.now().minusDays(60)
            )

            // Save friendships
            friendsRepository.saveAll(
                listOf(
                    friendship1,
                    friendship2,
                    friendship3,
                    friendship4,
                    friendship5,
                    friendship6,
                    friendship7,
                    friendship8,
                    friendship9,
                    friendship10,
                    friendship11,
                    friendship12
                )
            )

            // Flush to ensure friendships are saved
            entityManager.flush()

            // Create pending friend requests with funny scenarios
            // JuanitoAlimaña sent a request to PakitoChokolatero
            val friendRequest1 = FriendRequest(
                requester = savedPlayer4, player = savedPlayer1
            )

            // MarianoRajoyB sent a request to PakitoChokolatero (politician seeking chocolate!)
            val friendRequest2 = FriendRequest(
                requester = savedPlayer7, player = savedPlayer1
            )

            // FedeToroSalvaje sent a request to ManolitoElCabesa (wild bull meets big head)
            val friendRequest3 = FriendRequest(
                requester = savedPlayer8, player = savedPlayer2
            )

            // TriniLaBorracha sent a request to LolitoFdezGaming (drunk wants to play)
            val friendRequest4 = FriendRequest(
                requester = savedPlayer10, player = savedPlayer5
            )

            // MarianoRajoyB sent a request to TriniLaBorracha (politician meets drunk)
            val friendRequest5 = FriendRequest(
                requester = savedPlayer7, player = savedPlayer10
            )

            // Save friend requests
            friendRequestRepository.saveAll(
                listOf(
                    friendRequest1, friendRequest2, friendRequest3, friendRequest4, friendRequest5
                )
            )

            // Flush to ensure friend requests are saved
            entityManager.flush()

            // Create games - several different game scenarios
            // Short game (3 minutes)
            val game1 = Game(
                duration = 180 // 3 minutes
            )

            // Medium game (5 minutes)
            val game2 = Game(
                duration = 300 // 5 minutes
            )

            // Long game (10 minutes)
            val game3 = Game(
                duration = 600 // 10 minutes
            )

            // Very long game (15 minutes)
            val game4 = Game(
                duration = 900 // 15 minutes
            )

            // Save games
            val savedGame1 = gameRepository.save(game1)
            val savedGame2 = gameRepository.save(game2)
            val savedGame3 = gameRepository.save(game3)
            val savedGame4 = gameRepository.save(game4)

            // Flush to ensure games are saved
            entityManager.flush()

            // Create player-game relationships
            // Game 1: Short game with 3 players (PakitoChokolatero, ManolitoElCabesa, PepitoGrilloLoco)
            // PepitoGrilloLoco is the murderer
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

            // Game 2: Medium game with 4 players
            // (ManolitoElCabesa, PepitoGrilloLoco, JuanitoAlimaña, LolitoFdezGaming)
            // JuanitoAlimaña is the murderer (living up to his nickname!)
            val playerGame4 = PlayerGame(
                player = savedPlayer2,
                game = savedGame2,
                role = GameRole.INNOCENT,
                gameDateTime = LocalDateTime.now().minusDays(3)
            )

            val playerGame5 = PlayerGame(
                player = savedPlayer3,
                game = savedGame2,
                role = GameRole.INNOCENT,
                gameDateTime = LocalDateTime.now().minusDays(3)
            )

            val playerGame6 = PlayerGame(
                player = savedPlayer4,
                game = savedGame2,
                role = GameRole.MURDERER,
                gameDateTime = LocalDateTime.now().minusDays(3)
            )

            val playerGame7 = PlayerGame(
                player = savedPlayer5,
                game = savedGame2,
                role = GameRole.INNOCENT,
                gameDateTime = LocalDateTime.now().minusDays(3)
            )

            // Game 3: Long game with 5 players (LolitoFdezGaming, CarmelaLaChoni,
            // MarianoRajoyB, FedeToroSalvaje, ConchaVelascOh)
            // FedeToroSalvaje is the murderer (wild bull on the loose!)
            val playerGame8 = PlayerGame(
                player = savedPlayer5,
                game = savedGame3,
                role = GameRole.INNOCENT,
                gameDateTime = LocalDateTime.now().minusDays(1)
            )

            val playerGame9 = PlayerGame(
                player = savedPlayer6,
                game = savedGame3,
                role = GameRole.INNOCENT,
                gameDateTime = LocalDateTime.now().minusDays(1)
            )

            val playerGame10 = PlayerGame(
                player = savedPlayer7,
                game = savedGame3,
                role = GameRole.INNOCENT,
                gameDateTime = LocalDateTime.now().minusDays(1)
            )

            val playerGame11 = PlayerGame(
                player = savedPlayer8,
                game = savedGame3,
                role = GameRole.MURDERER,
                gameDateTime = LocalDateTime.now().minusDays(1)
            )

            val playerGame12 = PlayerGame(
                player = savedPlayer9,
                game = savedGame3,
                role = GameRole.INNOCENT,
                gameDateTime = LocalDateTime.now().minusDays(1)
            )

            // Game 4: Very long epic game with 8 players (all except Trini and Mariano)
            // CarmelaLaChoni is the murderer (surprise!)
            val playerGame13 = PlayerGame(
                player = savedPlayer1,
                game = savedGame4,
                role = GameRole.INNOCENT,
                gameDateTime = LocalDateTime.now().minusHours(12)
            )

            val playerGame14 = PlayerGame(
                player = savedPlayer2,
                game = savedGame4,
                role = GameRole.INNOCENT,
                gameDateTime = LocalDateTime.now().minusHours(12)
            )

            val playerGame15 = PlayerGame(
                player = savedPlayer3,
                game = savedGame4,
                role = GameRole.INNOCENT,
                gameDateTime = LocalDateTime.now().minusHours(12)
            )

            val playerGame16 = PlayerGame(
                player = savedPlayer4,
                game = savedGame4,
                role = GameRole.INNOCENT,
                gameDateTime = LocalDateTime.now().minusHours(12)
            )

            val playerGame17 = PlayerGame(
                player = savedPlayer5,
                game = savedGame4,
                role = GameRole.INNOCENT,
                gameDateTime = LocalDateTime.now().minusHours(12)
            )

            val playerGame18 = PlayerGame(
                player = savedPlayer6,
                game = savedGame4,
                role = GameRole.MURDERER,
                gameDateTime = LocalDateTime.now().minusHours(12)
            )

            val playerGame19 = PlayerGame(
                player = savedPlayer8,
                game = savedGame4,
                role = GameRole.INNOCENT,
                gameDateTime = LocalDateTime.now().minusHours(12)
            )

            val playerGame20 = PlayerGame(
                player = savedPlayer9,
                game = savedGame4,
                role = GameRole.INNOCENT,
                gameDateTime = LocalDateTime.now().minusHours(12)
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
                    playerGame8,
                    playerGame9,
                    playerGame10,
                    playerGame11,
                    playerGame12,
                    playerGame13,
                    playerGame14,
                    playerGame15,
                    playerGame16,
                    playerGame17,
                    playerGame18,
                    playerGame19,
                    playerGame20
                )
            )

            println("Sample data has been initialized!")
        } catch (e: Exception) {
            println("Error loading sample data: ${e.message}")
            e.printStackTrace()
        }
    }
}