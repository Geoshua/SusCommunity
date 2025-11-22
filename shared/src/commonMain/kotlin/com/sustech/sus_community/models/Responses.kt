package com.sustech.sus_community.models

import kotlinx.serialization.Serializable

@Serializable
data class UserListResponse(val users: List<User>, val count: Int)

/**
 * Response payload after successfully creating a user.
 *
 * @property user The newly created user
 * @property message Success message
 */
@Serializable
data class CreateUserResponse(
    val user: User,
    val message: String = "User created successfully"
)
