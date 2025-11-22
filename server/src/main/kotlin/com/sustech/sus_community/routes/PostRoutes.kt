package com.sustech.sus_community.routes

import com.sustech.sus_community.database.PostRepository
import com.sustech.sus_community.database.UserRepository
import com.sustech.sus_community.models.CreatePostRequest
import com.sustech.sus_community.models.CreatePostResponse
import com.sustech.sus_community.models.PostListResponse
import com.sustech.sus_community.models.Post
import com.sustech.sus_community.models.PostStatus
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.Instant
import java.util.UUID

/**
 * Repository for database operations on posts.
 * Uses SQLDelight for persistent storage.
 */
private val postRepository = PostRepository()

/**
 * Repository for database operations on users.
 */
private val userRepository = UserRepository()

/**
 * Configures routing for post-related endpoints.
 *
 * This function defines all HTTP endpoints for managing posts in the "Gig" & Volunteering Board.
 * Currently implements:
 * - POST /posts - Create a new post/request
 * - GET /posts - Retrieve all posts (TODO: add filtering by tag, location, status)
 * - GET /posts/{id} - Retrieve a specific post by ID (TODO)
 * - PUT /posts/{id} - Update a post (TODO)
 * - DELETE /posts/{id} - Delete a post (TODO)
 *
 * @receiver Route The route to configure post endpoints on
 */
fun Route.postRoutes() {
    route("/posts") {
        /**
         * POST /posts
         *
         * Creates a new post/request in the volunteering board.
         *
         * Request body (JSON):
         * {
         *   "title": "Need help moving furniture",
         *   "description": "Moving to a new apartment, need help with heavy items",
         *   "location": {
         *     "latitude": 48.1351,
         *     "longitude": 11.5820,
         *     "address": "Marienplatz, Munich"
         *   },
         *   "tag": "MOVING_HELP"
         * }
         *
         * Response (201 Created):
         * {
         *   "post": {
         *     "id": "uuid-here",
         *     "title": "Need help moving furniture",
         *     "description": "Moving to a new apartment, need help with heavy items",
         *     "location": { ... },
         *     "tag": "MOVING_HELP",
         *     "authorId": "temp-user-id",
         *     "createdAt": "2025-11-22T10:30:00Z",
         *     "status": "OPEN"
         *   },
         *   "message": "Post created successfully"
         * }
         *
         * Error responses:
         * - 400 Bad Request: Invalid input (missing fields, invalid coordinates, etc.)
         * - 500 Internal Server Error: Server-side error
         */
        post {
            try {
                // Parse the incoming request body
                val request = call.receive<CreatePostRequest>()

                // Validate the request
                val validationError = validateCreatePostRequest(request)
                if (validationError != null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to validationError))
                    return@post
                }

                // Validate that the user exists
                if (!userRepository.userExists(request.authorId)) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "User '${request.authorId}' not found. Please create a user first.")
                    )
                    return@post
                }

                // Create the post with server-generated fields
                val newPost = Post(
                    id = UUID.randomUUID().toString(),
                    title = request.title,
                    description = request.description,
                    location = request.location,
                    tag = request.tag,
                    dueDate = request.dueDate,
                    femaleOnly = request.femaleOnly,
                    images = request.images,
                    authorId = request.authorId, // Use username as authorId
                    createdAt = Instant.now().toString(),
                    status = PostStatus.OPEN
                )

                // Store the post in the database
                postRepository.insertPost(newPost)

                // Log the creation
                call.application.environment.log.info(
                    "Created post: id=${newPost.id}, title='${newPost.title}', tag=${newPost.tag}, author=${newPost.authorId}"
                )

                // Return success response
                call.respond(
                    HttpStatusCode.Created,
                    CreatePostResponse(post = newPost)
                )
            } catch (e: Exception) {
                // Log the error
                call.application.environment.log.error("Error creating post", e)
                e.printStackTrace()

                // Return error response
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Failed to create post: ${e.message}")
                )
            }
        }

        /**
         * GET /posts
         *
         * Retrieves all posts.
         * TODO: Add query parameters for filtering:
         * - tag: Filter by PostTag
         * - status: Filter by PostStatus
         * - lat, lng, radius: Filter by location proximity
         *
         * Response (200 OK):
         * {
         *   "posts": [ ... ],
         *   "count": 42
         * }
         */
        get {
            val allPosts = postRepository.getAllPosts()
            call.respond(
                HttpStatusCode.OK,
                PostListResponse(
                    posts = allPosts,
                    count = allPosts.size
                )
            )
        }

        /**
         * GET /posts/{id}
         *
         * Retrieves a specific post by its ID.
         */
        get("/{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Post ID is required")
                )
                return@get
            }

            val post = postRepository.getPostById(id)

            if (post != null) {
                call.respond(HttpStatusCode.OK, post)
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("error" to "Post not found")
                )
            }
        }

        /**
         * PUT /posts/{id}
         *
         * Updates an existing post.
         *
         * Request body (JSON) - same as POST /posts
         */
        put("/{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Post ID is required")
                )
                return@put
            }

            // Check if post exists
            val existingPost = postRepository.getPostById(id)
            if (existingPost == null) {
                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("error" to "Post not found")
                )
                return@put
            }

            try {
                val request = call.receive<CreatePostRequest>()

                // Validate the request
                val validationError = validateCreatePostRequest(request)
                if (validationError != null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to validationError))
                    return@put
                }

                // Create updated post object (keep original id, createdAt, authorId)
                val updatedPost = Post(
                    id = id,
                    title = request.title,
                    description = request.description,
                    location = request.location,
                    tag = request.tag,
                    dueDate = request.dueDate,
                    femaleOnly = request.femaleOnly,
                    images = request.images,
                    authorId = existingPost.authorId, // Keep original author
                    createdAt = existingPost.createdAt, // Keep original creation time
                    status = existingPost.status // Keep current status
                )

                // Update the post in the database
                postRepository.updatePost(id, updatedPost)

                call.respond(HttpStatusCode.OK, mapOf("post" to updatedPost))
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Invalid request body: ${e.message}")
                )
            }
        }

        /**
         * DELETE /posts/{id}
         *
         * Deletes a post by its ID.
         */
        delete("/{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Post ID is required")
                )
                return@delete
            }

            // Check if post exists
            val existingPost = postRepository.getPostById(id)
            if (existingPost == null) {
                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("error" to "Post not found")
                )
                return@delete
            }

            // Delete the post
            postRepository.deletePostById(id)

            call.respond(
                HttpStatusCode.OK,
                mapOf("message" to "Post deleted successfully", "id" to id)
            )
        }
    }
}

/**
 * Validates a CreatePostRequest.
 *
 * Ensures that:
 * - Username is not blank
 * - Title is not blank and within length limits
 * - Description is not blank and within length limits
 * - Location coordinates are valid (latitude: -90 to 90, longitude: -180 to 180)
 * - Due date is provided and in valid ISO 8601 format
 * - Images list doesn't exceed maximum allowed (10 images)
 *
 * @param request The request to validate
 * @return Error message if validation fails, null if valid
 */
private fun validateCreatePostRequest(request: CreatePostRequest): String? {
    // Validate username
    if (request.authorId.isBlank()) {
        return "AuthorID cannot be blank"
    }

    // Validate title
    if (request.title.isBlank()) {
        return "Title cannot be blank"
    }
    if (request.title.length > 200) {
        return "Title must be 200 characters or less"
    }

    // Validate description
    if (request.description.isBlank()) {
        return "Description cannot be blank"
    }
    if (request.description.length > 2000) {
        return "Description must be 2000 characters or less"
    }

    // Validate location coordinates
    if (request.location.latitude < -90 || request.location.latitude > 90) {
        return "Latitude must be between -90 and 90"
    }
    if (request.location.longitude < -180 || request.location.longitude > 180) {
        return "Longitude must be between -180 and 180"
    }

    // Validate due date (basic format check - should be ISO 8601)
    if (request.dueDate.isBlank()) {
        return "Due date cannot be blank"
    }
    try {
        Instant.parse(request.dueDate)
    } catch (e: Exception) {
        return "Due date must be in ISO 8601 format (e.g., 2025-12-25T14:00:00Z)"
    }

    // Validate images
    if (request.images.size > 10) {
        return "Maximum 10 images allowed per post"
    }

    // Basic URL validation for images
    for (imageUrl in request.images) {
        if (imageUrl.isBlank()) {
            return "Image URLs cannot be blank"
        }
        if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
            return "Image URLs must start with http:// or https://"
        }
    }

    return null
}
