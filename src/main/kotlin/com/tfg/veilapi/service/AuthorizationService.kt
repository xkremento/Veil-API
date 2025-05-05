package com.tfg.veilapi.service

import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class AuthorizationService {

    /**
     * Checks if the authenticated user matches the requested email
     * @param requestedEmail The target email
     * @throws ResponseStatusException If unauthorized
     */

    fun validateUserAccess(requestedEmail: String) {
        val authentication = SecurityContextHolder.getContext().authentication
        val currentUserEmail = authentication.name

        if (currentUserEmail != requestedEmail) {
            throw ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Access to this resource is denied."
            )
        }
    }

    /**
     * Checks if the authenticated user is in the list of allowed emails
     * @param allowedEmails List of emails with access
     * @throws ResponseStatusException If unauthorized
     */

    fun validateUserIsInList(allowedEmails: List<String>) {
        val authentication = SecurityContextHolder.getContext().authentication
        val currentUserEmail = authentication.name

        if (!allowedEmails.contains(currentUserEmail)) {
            throw ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Access to this resource is denied."
            )
        }
    }

    /**
     * Retrieves the email of the currently authenticated user
     * @return The authenticated user's email
     */

    fun getCurrentUserEmail(): String {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication.name
    }
}