package com.tfg.veilapi.controller

import com.tfg.veilapi.dto.AuthRequestDTO
import com.tfg.veilapi.dto.AuthResponseDTO
import com.tfg.veilapi.dto.PlayerRegistrationDTO
import com.tfg.veilapi.dto.PlayerResponseDTO
import com.tfg.veilapi.security.JwtTokenUtil
import com.tfg.veilapi.security.JwtUserDetailsService
import com.tfg.veilapi.service.PlayerService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Authentication", description = "Authentication API for login and registration")
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenUtil: JwtTokenUtil,
    private val jwtUserDetailsService: JwtUserDetailsService,
    private val playerService: PlayerService
) {
    @Operation(
        summary = "Authenticate user",
        description = "Authenticate a user with email and password and return a JWT token"
    )
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Successful authentication",
            content = [Content(schema = Schema(implementation = AuthResponseDTO::class))]
        ), ApiResponse(
            responseCode = "401", description = "Authentication failed", content = [Content()]
        )]
    )

    @PostMapping("/login")
    fun createAuthenticationToken(@RequestBody authRequest: AuthRequestDTO): ResponseEntity<AuthResponseDTO> {
        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(authRequest.email, authRequest.password)
            )
        } catch (e: BadCredentialsException) {
            throw BadCredentialsException("Incorrect email or password")
        }

        val userDetails: UserDetails = jwtUserDetailsService.loadUserByUsername(authRequest.email)
        val token: String = jwtTokenUtil.generateToken(userDetails)
        val player = playerService.getPlayer(authRequest.email)

        return ResponseEntity.ok(
            AuthResponseDTO(
                token = token, email = player.email, nickname = player.nickname
            )
        )
    }

    @Operation(
        summary = "Register a new player", description = "Register a new player with email, nickname, and password"
    )
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Player registered successfully",
            content = [Content(schema = Schema(implementation = PlayerResponseDTO::class))]
        ), ApiResponse(
            responseCode = "409", description = "Player with this email already exists", content = [Content()]
        )]
    )

    @PostMapping("/register")
    fun registerUser(@Valid @RequestBody playerDto: PlayerRegistrationDTO): ResponseEntity<PlayerResponseDTO> {
        return ResponseEntity.ok(playerService.registerPlayer(playerDto))
    }
}