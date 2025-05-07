# Veil:Tricked API

A robust REST API for the game "Veil:Tricked".

## Overview

Veil:Tricked is a social deduction game where players participate in matches, and one of them is assigned the role of "murderer". The API provides all the necessary endpoints to handle player authentication, friend relationships, and game management.

## Technologies Used

- **Backend**: Kotlin with Spring Boot
- **Security**: JWT Authentication & Authorization
- **Database**: MySQL
- **Documentation**: Swagger/OpenAPI
- **Dependencies Management**: Gradle
- **Validation**: Jakarta Bean Validation

## Features

### Authentication
- User registration with email validation
- Login with JWT token generation
- Role-based authorization (User/Admin)

### Player Management
- Create and update player profiles
- Customize player skins
- In-game virtual currency system
- View player details and statistics

### Social Features
- Send and receive friend requests
- Accept or decline friendship requests
- View list of friends and pending requests
- Remove friends

### Game Management
- Create new game sessions
- Assign roles to players (murderer/innocent)
- View game details with player information
- Track game history

### Admin Functionality
- Manage player roles (promote/demote admins)
- Add coins to player accounts
- View detailed player information

## API Endpoints

### Authentication Endpoints
- `POST /api/auth/register` - Register a new player
- `POST /api/auth/login` - Login and receive JWT token

### Player Endpoints
- `GET /api/players/me` - Get current player details
- `PUT /api/players` - Update player information
- `DELETE /api/players` - Delete player account

### Friends Endpoints
- `POST /api/friends/requests` - Send a friend request
- `POST /api/friends/requests/{requestId}/accept` - Accept a friend request
- `POST /api/friends/requests/{requestId}/decline` - Decline a friend request
- `GET /api/friends/requests` - Get all pending friend requests
- `GET /api/friends` - Get all friends
- `DELETE /api/friends/{friendEmail}` - Remove a friend

### Game Endpoints
- `POST /api/games` - Create a new game
- `GET /api/games/{gameId}` - Get game details
- `GET /api/games` - Get all games for current player

### Admin Endpoints
- `GET /api/admin/players/{email}` - Get details for any player
- `POST /api/admin/players/{email}/coins` - Add coins to a player
- `POST /api/admin/players/{email}/roles/admin` - Assign admin role
- `DELETE /api/admin/players/{email}/roles/admin` - Remove admin role
- `PUT /api/admin/players/{email}/skin` - Update a player's skin URL
## Documentation

The API is fully documented using Swagger/OpenAPI. When running the application, you can access the interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

This provides a comprehensive view of all available endpoints, their parameters, request/response models, and allows for testing the API directly from the browser.

## Security

Security is a top priority for this API:

- JWT tokens for authentication with configurable expiration time
- Role-based access control for endpoints
- Password encryption using BCrypt
- Input validation on all endpoints
- Protection against SQL injection and XSS attacks
- CORS configuration for secure cross-origin requests

## Error Handling

The API provides consistent error responses across all endpoints, with appropriate HTTP status codes and descriptive error messages to facilitate debugging and improve the user experience.