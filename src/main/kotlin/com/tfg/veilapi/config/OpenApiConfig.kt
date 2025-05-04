package com.tfg.veilapi.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    info = Info(
        title = "Veil:Tricked API",
        version = "1.0.0",
        description = "API for the Game Veil:Tricked</br>Authors: Álvaro Fernández, Kida García & Ahmed Hassan"
    )
)
@SecurityScheme(
    name = "bearerAuth",
    description = "JWT authentication",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    `in` = SecuritySchemeIn.HEADER
)
class OpenApiConfig