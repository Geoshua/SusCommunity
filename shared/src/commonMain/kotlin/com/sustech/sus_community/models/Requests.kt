package com.sustech.sus_community.models

import kotlinx.serialization.Serializable

/**
 * Request payload for creating a new post.
 *
 * @property title Brief title of the post/request
 * @property description Detailed description of what help is needed
 * @property location Geographic location where help is needed
 * @property tag Category of the request (e.g., PET_SITTING, TUTORING)
 * @property dueDate Due date/time for the request (ISO 8601 format)
 * @property femaleOnly If true, only female volunteers should respond
 * @property images List of image URLs associated with this post
 * @property username The username of the author
 */
@Serializable
data class CreatePostRequest(
    val title: String,
    val description: String,
    val location: Location,
    val tag: PostTag,
    val dueDate: String,
    val femaleOnly: Boolean = false,
    val images: List<String> = emptyList(),
    val authorId: String
)

/**
 * Request payload for creating a new user.
 *
 * @property username User's chosen username
 * @property displayName User's display name (optional)
 * @property role User's role in the community
 * @property age User's age
 * @property gender User's gender
 * @property hasPets Whether user has pets
 * @property petTypes Types of pets (optional)
 * @property bio Short user bio (optional)
 */
@Serializable
data class CreateUserRequest(
    val username: String,
    val displayName: String? = null,
    val role: UserRole = UserRole.NEW_MUENCHER,
    val age: Int? = null,
    val gender: Gender? = null,
    val hasPets: Boolean = false,
    val petTypes: List<String> = emptyList(),
    val bio: String? = null
)
