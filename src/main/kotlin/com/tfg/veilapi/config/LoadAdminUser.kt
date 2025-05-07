package com.tfg.veilapi.config

import com.tfg.veilapi.model.Player
import com.tfg.veilapi.repository.PlayerRepository
import com.tfg.veilapi.service.PlayerService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class LoadAdminUser(
    private val playerRepository: PlayerRepository,
    private val playerService: PlayerService,
    private val passwordEncoder: PasswordEncoder,
    @Value("\${app.admin.email:admin@veil.com}") private val adminEmail: String,
    @Value("\${app.admin.password:adminPassword}") private val adminPassword: String,
    @Value("\${app.admin.nickname:admin}") private val adminNickname: String
) : CommandLineRunner {

    @Transactional
    override fun run(vararg args: String?) {
        // Crear un usuario administrador si no existe
        if (!playerRepository.existsById(adminEmail)) {
            val adminPlayer = Player(
                email = adminEmail,
                nickname = adminNickname,
                password = passwordEncoder.encode(adminPassword)
            )

            playerRepository.save(adminPlayer)
            // Asignar rol de administrador
            playerService.addAdminRole(adminEmail)

            println("Admin user created: $adminEmail")
        }
    }
}