# Veil:Tricked API

A robust REST API for the game "Veil:Tricked".

## Overview

Veil:Tricked is a social deduction game where players participate in matches, and one of them is assigned the role of "murderer". The API provides all the necessary endpoints to handle player authentication, friend relationships, and game management.

## Technologies Used

- **Backend**: Kotlin with Spring Boot 3.4.4
- **Security**: JWT Authentication & Spring Security
- **Database**: MySQL (AWS RDS)
- **Documentation**: Swagger/OpenAPI 3.0
- **Build Tool**: Gradle with Kotlin DSL
- **Java Version**: 17
- **Validation**: Jakarta Bean Validation

## Features

### Authentication
- User registration with email validation
- Login with JWT token generation
- Role-based authorization (USER/ADMIN)
- Token expiration handling

### Player Management
- Player profiles with customizable profile images
- Secure password management
- Skin customization (admin-only feature)
- In-game virtual currency system
- View player details and statistics

### Social Features
- Send and receive friend requests
- Accept or decline friendship requests
- View list of friends with friendship dates
- View pending friend requests
- Remove friends (bidirectional removal)

### Game Management
- Create new game sessions
- Assign roles to players (murderer/innocent)
- View game details with player information
- Track game history
- Check if the player was the murderer in a specific game
- Admin ability to change the murderer in a game

### Admin Functionality
- Manage player roles (promote/demote admins)
- Update any player's nickname, skin, or profile image
- Add coins to player accounts
- View detailed information for any player
- Create and manage game sessions
- Change murderer assignments in existing games

## API Endpoints

### Authentication Endpoints
- `POST /api/auth/register` - Register a new player
- `POST /api/auth/login` - Login and receive JWT token

### Player Endpoints
- `GET /api/players/me` - Get current player details
- `PUT /api/players/password` - Update player password
- `PUT /api/players/profile-image` - Update player profile image
- `DELETE /api/players` - Delete player account

### Friends Endpoints
- `POST /api/friends/requests` - Send a friend request
- `POST /api/friends/requests/{requestId}/accept` - Accept a friend request
- `POST /api/friends/requests/{requestId}/decline` - Decline a friend request
- `GET /api/friends/requests` - Get all pending friend requests
- `GET /api/friends` - Get all friends
- `DELETE /api/friends/{friendEmail}` - Remove a friend

### Game Endpoints
- `GET /api/games/{gameId}` - Get game details (players can only view games they participated in)
- `GET /api/games` - Get all games for current player
- `GET /api/games/{gameId}/was-murderer` - Check if the authenticated player was the murderer

### Admin Endpoints
- `GET /api/admin/players/{email}` - Get details for any player
- `POST /api/admin/players/{email}/coins` - Add coins to a player
- `POST /api/admin/players/{email}/roles/admin` - Assign admin role
- `DELETE /api/admin/players/{email}/roles/admin` - Remove admin role
- `PUT /api/admin/players/{email}/skin` - Update a player's skin URL
- `PUT /api/admin/players/{email}/nickname` - Update a player's nickname
- `PUT /api/admin/players/{email}/profile-image` - Update a player's profile image
- `POST /api/admin/games` - Create a new game session
- `PUT /api/admin/games/{gameId}/set-murderer/{playerEmail}` - Change the murderer in a game

## Documentation

The API is fully documented using Swagger/OpenAPI. When running the application, you can access the interactive API documentation at:
```
http://serversipaddress:8080/swagger-ui.html
```

This provides a comprehensive view of all available endpoints, their parameters, request/response models, and allows for testing the API directly from the browser.

## Security

Security features implemented:

- **JWT Authentication**: Stateless authentication with configurable expiration
- **Role-based Access Control**: USER and ADMIN roles with specific permissions
- **Password Encryption**: BCrypt with salt for secure password storage
- **Authorization Service**: Validates user access to resources
- **Input Validation**: All inputs validated using Jakarta Bean Validation
- **SQL Injection Protection**: Using JPA parameterized queries
- **Restricted Operations**: Users can only modify their own data (except admins)

## Database Schema

The application uses MySQL with the following main entities:
- **Player**: User accounts with authentication and profile information
- **Role**: User roles (USER, ADMIN)
- **Game**: Game sessions with duration
- **PlayerGame**: Many-to-many relationship between players and games with role assignment
- **Friends**: Bidirectional friendship relationships
- **FriendRequest**: Pending friendship requests

## Configuration

Key configuration properties in `application.properties`:
- Server port: 8080
- JWT expiration: 24 hours (configurable)
- Database: MySQL on AWS RDS
- Hibernate DDL: auto-update
- Admin account created on startup (configurable)
- Sample data loading (configurable)

## Error Handling

The API provides consistent error responses:
- **400 Bad Request**: Invalid input or business rule violation
- **401 Unauthorized**: Missing or invalid authentication
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: Resource not found
- **409 Conflict**: Duplicate resources or conflicting state

All errors include descriptive messages to help with debugging.
he API provides consistent error responses across all endpoints, with appropriate HTTP status codes and descriptive error messages to facilitate debugging and improve the user experience.

## Sample Data

When `app.data.load-sample=true`, the application loads sample data including:
- Admin user account
- Sample players with Spanish names
- Friend relationships
- Sample games with assigned roles

This is useful for development and testing purposes.
