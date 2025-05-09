package com.tfg.veilapi.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtRequestFilter(
    private val userDetailsService: JwtUserDetailsService, private val jwtTokenUtil: JwtTokenUtil
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain
    ) {
        val authorizationHeader = request.getHeader("Authorization")
        var username: String? = null
        var jwtToken: String? = null

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7)
            try {
                username = jwtTokenUtil.extractUsername(jwtToken)
            } catch (e: Exception) {
                logger.error("Failed to extract username from JWT token", e)
            }
        }

        if (username != null && SecurityContextHolder.getContext().authentication == null) {
            val userDetails = userDetailsService.loadUserByUsername(username)
            if (jwtToken != null && jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                val authenticationToken = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.authorities
                )
                authenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authenticationToken
            }
        }

        chain.doFilter(request, response)
    }
}