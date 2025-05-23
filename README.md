# Veil:Tricked API

A robust REST API for the game "Veil:Tricked" built with modern security practices and comprehensive validation.

## Overview

Veil:Tricked is a social deduction game where players participate in matches, and one of them is assigned the role of "murderer". The API provides all the necessary endpoints to handle player authentication, friend relationships, and game management with enterprise-grade security and validation.

## Technologies Used

- **Backend**: Kotlin with Spring Boot 3.4.4
- **Security**: JWT Authentication & Spring Security with role-based access control
- **Database**: MySQL (AWS RDS) with JPA/Hibernate
- **Documentation**: Swagger/OpenAPI 3.0 with comprehensive response documentation
- **Build Tool**: Gradle with Kotlin DSL
- **Java Version**: 17
- **Validation**: Jakarta Bean Validation with centralized validation constants
- **Error Handling**: Global exception handling with specific HTTP status codes

## Features

### Authentication & Security
- User registration with comprehensive email and password validation
- Login with JWT token generation (24-hour expiration)
- Role-based authorization (USER/ADMIN) with granular permissions
- Token expiration handling and secure password encryption
- Protection against self-referencing operations and unauthorized access

### Player Management
- Player profiles with customizable profile images and skins
- Secure password management with pattern validation
- Nickname uniqueness validation and conflict detection
- In-game virtual currency system (max 999,999 coins)
- User data ownership validation (users can only modify their own data)

### Social Features
- Send and receive friend requests with duplicate prevention
- Accept or decline friendship requests with authorization checks
- View list of friends with friendship dates and profile information
- View pending friend requests for authenticated user only
- Remove friends (bidirectional removal) with proper validation
- Prevention of self-friend requests

### Game Management
- Create new game sessions with participant validation
- Assign roles to players (murderer/innocent) with business rule validation
- View game details with authorization (only participants can view)
- Track comprehensive game history for authenticated users
- Check murderer status with privacy protection
- Admin ability to modify game roles with proper authorization

### Admin Functionality
- Comprehensive player management (roles, coins, profiles)
- Nickname conflict detection and validation
- Profile image and skin URL validation with size limits
- Add coins to player accounts with maximum limit enforcement
- Create and manage game sessions with enhanced validation
- Change murderer assignments with conflict resolution

## API Endpoints

### Authentication Endpoints
- `POST /api/auth/register` - Register a new player with validation
- `POST /api/auth/login` - Login and receive JWT token

### Player Endpoints (Authenticated Users)
- `GET /api/players/me` - Get current player details
- `PUT /api/players/password` - Update password with pattern validation
- `PUT /api/players/profile-image` - Update profile image with URL validation
- `DELETE /api/players` - Delete own account

### Friends Endpoints (Authenticated Users)
- `POST /api/friends/requests` - Send friend request (no self-requests)
- `POST /api/friends/requests/{requestId}/accept` - Accept friend request (authorization validated)
- `DELETE /api/friends/requests/{requestId}` - Reject/cancel friend request
- `GET /api/friends/requests` - Get pending requests for authenticated user
- `GET /api/friends` - Get friends list for authenticated user
- `DELETE /api/friends/{friendEmail}` - Remove friend (bidirectional)

### Game Endpoints (Authenticated Users)
- `POST /api/games` - Create game (creator must be participant)
- `GET /api/games/{gameId}` - Get game details (participants only)
- `GET /api/games/my-games` - Get games for authenticated user
- `GET /api/games/{gameId}/murderer-check` - Check own murderer status

### Admin Endpoints (Admin Role Required)
- `GET /api/admin/players/{email}` - Get any player details
- `PUT /api/admin/players/{email}` - Update player with conflict detection
- `DELETE /api/admin/players/{email}` - Delete any player
- `POST /api/admin/players/{email}/coins` - Add coins with limit validation
- `PUT /api/admin/players/{email}/nickname` - Update nickname with uniqueness check
- `PUT /api/admin/players/{email}/profile-image` - Update any player's profile image
- `PUT /api/admin/players/{email}/skin` - Update any player's skin
- `POST /api/admin/players/{email}/admin-role` - Grant admin role
- `DELETE /api/admin/players/{email}/admin-role` - Revoke admin role
- `PUT /api/games/{gameId}/murderer/{playerEmail}` - Change game murderer

## Documentation

The API features comprehensive Swagger/OpenAPI documentation with detailed response codes and error scenarios. Access the interactive documentation at:
```
http://serversipaddress:8080/swagger-ui.html
```

Features include:
- Complete request/response examples
- HTTP status code documentation
- Error response schemas
- Authentication requirements
- Field validation rules

## Security & Validation

### Security Features
- **JWT Authentication**: Stateless with configurable expiration
- **Role-based Access Control**: Granular permissions with resource ownership validation
- **Password Security**: BCrypt encryption with complexity requirements
- **Authorization Service**: Validates user access to specific resources
- **Input Validation**: Centralized validation constants with comprehensive rules
- **SQL Injection Protection**: JPA parameterized queries
- **Resource Ownership**: Users can only access their own data (except admins)

### Validation Rules
- **Passwords**: Minimum 8 characters, must contain uppercase, lowercase, number, and special character
- **Nicknames**: 3-30 characters, alphanumeric and underscores only, must be unique
- **Emails**: Valid format, maximum 254 characters
- **URLs**: Valid format, maximum 2048 characters
- **Coins**: 1-999,999 range validation
- **Game Duration**: 60-3600 seconds (1 minute to 1 hour)
- **Players per Game**: 2-10 participants

## Error Handling

The API provides consistent, detailed error responses with appropriate HTTP status codes:

- **400 Bad Request**: Invalid input, business rule violations, or malformed data

- **401 Unauthorized**: Missing or invalid authentication
- **403 Forbidden**: Insufficient permissions or access to unauthorized resources
- **404 Not Found**: Resource not found
- **409 Conflict**: Duplicate resources, nickname conflicts, or already existing relationships

All errors include:
- Timestamp for debugging
- Specific error messages
- Field-level validation errors where applicable
- Consistent response structure

## Database Schema

MySQL database with optimized relationships:

### Core Entities
- **Player**: Authentication, profile, and game statistics
- **Role**: USER and ADMIN roles with permissions
- **Game**: Game sessions with duration and metadata
- **PlayerGame**: Player-game relationships with role assignments
- **Friends**: Bidirectional friendship relationships with timestamps
- **FriendRequest**: Pending friendship requests with requester/recipient

### Key Constraints
- Email as primary key for players (unique, not null)
- Nickname uniqueness constraint
- Foreign key relationships with proper cascading
- Indexes on frequently queried fields

## Configuration

### Application Properties
## Server Configuration
server.port=8080

## Database Configuration
spring.datasource.url=jdbc:mysql://[host]:[port]/veildb
spring.jpa.hibernate.ddl-auto=update

## JWT Configuration
jwt.secret=[256-bit-secret]
jwt.expiration=86400  # 24 hours

## Admin Configuration
app.admin.email=admin@veil.com
app.admin.password=adminPassword
app.admin.nickname=admin

## Data Loading
app.data.load-sample=true

### Sample Data

When `app.data.load-sample=true`, the application loads:
- Admin user account with full privileges
- Sample players with realistic Spanish names and profiles
- Established friend relationships for testing
- Sample games with various role assignments
- Friendship requests for testing workflows

Perfect for development, testing, and API exploration.

