package com.sustech.sus_community.routes

import com.sustech.sus_community.database.UserRepository
import com.sustech.sus_community.models.*
import com.sustech.sus_community.models.CreateUserRequest
import com.sustech.sus_community.models.CreateUserResponse
import com.sustech.sus_community.models.UserListResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.Instant

/**
 * Repository for database operations on users.
 */
private val userRepository = UserRepository()

/**
 * Configures routing for user-related endpoints.
 *
 * This function defines all HTTP endpoints for managing users.
 * For MVP: Simple username-based system, no authentication.
 *
 * Endpoints:
 * - POST /users - Create a new user
 * - GET /users - Retrieve all users
 * - GET /users/{username} - Retrieve a specific user by username
 */
fun Route.userRoutes() {
    route("/users") {
        /**
         * POST /users
         *
         * Creates a new user in the system.
         *
         * Request body (JSON):
         * {
         *   "username": "john_doe",
         *   "displayName": "John Doe",
         *   "role": "NEW_MUENCHER",
         *   "age": 35,
         *   "gender": "MALE",
         *   "hasPets": true,
         *   "petTypes": ["dog"],
         *   "bio": "New to Munich..."
         * }
         *
         * Response (201 Created):
         * {
         *   "user": { ... },
         *   "message": "User created successfully"
         * }
         */
        post {
            try {
                val request = call.receive<CreateUserRequest>()

                // Validate username
                if (request.username.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Username cannot be blank"))
                    return@post
                }
                if (request.username.length > 100) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Username must be 100 characters or less"))
                    return@post
                }

                // Check if username already exists (optional - as per MVP requirements)
                if (userRepository.userExists(request.username)) {
                    call.respond(
                        HttpStatusCode.Conflict,
                        mapOf("error" to "Username '${request.username}' already exists")
                    )
                    return@post
                }

                // Validate age if provided
                val age = request.age
                if (age != null && (age < 1 || age > 150)) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Age must be between 1 and 150"))
                    return@post
                }

                // Create new user with calculated green title
                val newUser = User(
                    username = request.username,
                    displayName = request.displayName,
                    role = request.role,
                    age = request.age,
                    gender = request.gender,
                    hasPets = request.hasPets,
                    petTypes = request.petTypes,
                    sustainabilityScore = 0,
                    greenTitle = GreenTitle.BEGINNER,  // Start as beginner
                    goodwillPoints = 0,
                    bio = request.bio,
                    createdAt = Instant.now().toString()
                )

                // Insert into database
                userRepository.insertUser(newUser)

                call.application.environment.log.info(
                    "Created user: username=${newUser.username}, role=${newUser.role}"
                )

                call.respond(
                    HttpStatusCode.Created,
                    CreateUserResponse(user = newUser)
                )
            } catch (e: Exception) {
                call.application.environment.log.error("Error creating user", e)
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Failed to create user: ${e.message}")
                )
            }
        }

        /**
         * GET /users
         *
         * Retrieves all users.
         *
         * Response (200 OK):
         * {
         *   "users": [ ... ],
         *   "count": 5
         * }
         */
        get {
            try {
                val allUsers = userRepository.getAllUsers()
                call.respond(
                    HttpStatusCode.OK,
                    UserListResponse(
                        users = allUsers,
                        count = allUsers.size
                    )
                )
            } catch (e: Exception) {
                call.application.environment.log.error("Error fetching users", e)
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Failed to fetch users: ${e.message}")
                )
            }
        }

        /**
         * GET /users/{username}
         *
         * Retrieves a specific user by username.
         *
         * Response (200 OK): Returns the user object
         * Response (404 Not Found): User not found
         */
        get("/{username}") {
            val username = call.parameters["username"]
            if (username.isNullOrBlank()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Username parameter is required")
                )
                return@get
            }

            try {
                val user = userRepository.getUserByUsername(username)
                if (user != null) {
                    call.respond(HttpStatusCode.OK, user)
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        mapOf("error" to "User '$username' not found")
                    )
                }
            } catch (e: Exception) {
                call.application.environment.log.error("Error fetching user $username", e)
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Failed to fetch user: ${e.message}")
                )
            }
        }
    }
}
