package com.sustech.sus_community.models

import kotlinx.serialization.Serializable

/**
 * User role in the community.
 * Based on the "NewMuenchers vs OldMuenchers" concept from the project requirements.
 */
@Serializable
enum class UserRole {
    NEW_MUENCHER,  // Newcomer needing help
    OLD_MUENCHER   // Local offering help
}

/**
 * User gender for matching and filtering.
 */
@Serializable
enum class Gender {
    MALE,
    FEMALE,
    NON_BINARY
}

/**
 * Green title/badge based on sustainability score.
 */
@Serializable
enum class GreenTitle {
    BEGINNER,           // 0-99 points
    ECO_CONSCIOUS,      // 100-249 points
    GREEN_WARRIOR,      // 250-499 points
    SUSTAINABILITY_HERO,// 500-999 points
    PLANET_CHAMPION     // 1000+ points
}

/**
 * Represents a user in the SusCommunity application.
 *
 * For MVP, authentication is simplified:
 * - Users are identified by their username only (no password)
 * - Username is used as the unique identifier
 * - No uniqueness constraints enforced at database level
 *
 * @property username User's chosen username (acts as ID)
 * @property displayName User's display name
 * @property role User's role in the community (NEW_MUENCHER or OLD_MUENCHER)
 * @property age User's age (for filtering elderly help, etc.)
 * @property gender User's gender (for gender-specific posts)
 * @property hasPets Whether user has pets (useful for pet-sitting posts)
 * @property petTypes Types of pets user has (e.g., "dog", "cat")
 * @property sustainabilityScore Gamification score for sustainable actions
 * @property greenTitle Title/badge based on sustainability score
 * @property bio Short user bio
 * @property createdAt Timestamp when the user joined (ISO 8601 format)
 */
@Serializable
data class User(
    val username: String,
    val displayName: String? = null,
    val role: UserRole = UserRole.NEW_MUENCHER,

    // Identifiable attributes
    val age: Int? = null,
    val gender: Gender? = null,
    val hasPets: Boolean = false,
    val petTypes: List<String> = emptyList(),  // e.g., ["dog", "cat", "bird"]

    // Sustainability/Goodwill tracking
    val sustainabilityScore: Int = 0,
    val greenTitle: GreenTitle = GreenTitle.BEGINNER,
    val goodwillPoints: Int = 0,  // Points for helping others

    // Profile
    val bio: String? = null,
    val createdAt: String? = null
) {
    /**
     * Calculate the green title based on sustainability score.
     */
    fun calculateGreenTitle(): GreenTitle {
        return when {
            sustainabilityScore >= 1000 -> GreenTitle.PLANET_CHAMPION
            sustainabilityScore >= 500 -> GreenTitle.SUSTAINABILITY_HERO
            sustainabilityScore >= 250 -> GreenTitle.GREEN_WARRIOR
            sustainabilityScore >= 100 -> GreenTitle.ECO_CONSCIOUS
            else -> GreenTitle.BEGINNER
        }
    }

    /**
     * Check if user is elderly (age 65+)
     */
    fun isElderly(): Boolean {
        return age != null && age >= 65
    }
}



