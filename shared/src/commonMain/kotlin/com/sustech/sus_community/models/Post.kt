package com.sustech.sus_community.models

import kotlinx.serialization.Serializable

/**
 * Represents the different types of requests/posts users can create.
 * These tags help categorize posts for filtering and matching volunteers.
 */
@Serializable
enum class PostTag {
    PET_SITTING,
    TUTORING,
    ELDERLY_COMPANY,
    MOWING,
    MOVING_HELP,
    EVENT,
    VOLUNTEERING,
    OTHER
}

/**
 * Represents a geographic location for a post.
 *
 * @property latitude Geographic latitude coordinate
 * @property longitude Geographic longitude coordinate
 * @property address Human-readable address (optional)
 */
@Serializable
data class Location(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null
)

/**
 * Represents a post/request in the "Gig" & Volunteering Board.
 * Users can create posts requesting help, which volunteers can accept.
 *
 * @property id Unique identifier for the post (server-generated)
 * @property title Brief title of the post/request
 * @property description Detailed description of what help is needed
 * @property location Geographic location where help is needed
 * @property tag Category of the request (e.g., PET_SITTING, TUTORING)
 * @property dueDate Due date/time for the request (ISO 8601 format)
 * @property femaleOnly If true, only female volunteers should respond
 * @property images List of image URLs associated with this post
 * @property authorId ID of the user who created this post
 * @property createdAt Timestamp when the post was created (ISO 8601 format)
 * @property status Current status of the post (OPEN, IN_PROGRESS, COMPLETED, CANCELLED)
 */
@Serializable
data class Post(
    val id: String? = null,  // null when creating a new post, server will generate
    val title: String,
    val description: String,
    val location: Location,
    val tag: PostTag,
    val dueDate: String,
    val femaleOnly: Boolean = false,
    val images: List<String> = emptyList(),  // List of image URLs
    val authorId: String? = null,  // Will be set by server based on authenticated user
    val createdAt: String? = null,  // Will be set by server (ISO 8601 timestamp)
    val status: PostStatus = PostStatus.OPEN
)

/**
 * Represents the lifecycle status of a post.
 */
@Serializable
enum class PostStatus {
    OPEN,           // Post is active and accepting volunteers
    IN_PROGRESS,    // A volunteer has been assigned
    COMPLETED,      // The task has been completed
    CANCELLED       // The post creator cancelled the request
}

/**
 * Request payload for creating a new post.
 * This is what clients send to the POST /posts endpoint.
 *
 * @property title Brief title of the post/request
 * @property description Detailed description of what help is needed
 * @property location Geographic location where help is needed
 * @property tag Category of the request
 * @property dueDate Due date/time for the request (ISO 8601 format, e.g., "2025-12-25T14:00:00Z")
 * @property femaleOnly If true, only female volunteers should respond (default: false)
 * @property images List of image URLs to attach to this post (optional)
 */
@Serializable
data class CreatePostRequest(
    val title: String,
    val description: String,
    val location: Location,
    val tag: PostTag,
    val dueDate: String,
    val femaleOnly: Boolean = false,
    val images: List<String> = emptyList()
)

/**
 * Response payload after successfully creating a post.
 *
 * @property post The newly created post with server-generated fields
 * @property message Success message
 */
@Serializable
data class CreatePostResponse(
    val post: Post,
    val message: String = "Post created successfully"
)

/**
 * Response payload for retrieving a list of posts.
 *
 * @property posts List of posts
 * @property count Total number of posts
 */
@Serializable
data class PostListResponse(
    val posts: List<Post>,
    val count: Int
)
