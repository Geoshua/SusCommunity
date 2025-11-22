package com.sustech.sus_community.database

import com.sustech.sus_community.models.Gender
import com.sustech.sus_community.models.GreenTitle
import com.sustech.sus_community.models.User
import com.sustech.sus_community.models.UserRole
import java.sql.ResultSet
import java.sql.Connection
import java.time.Instant

/**
 * Repository for managing User data in PostgreSQL.
 *
 * This class provides CRUD operations for users with their rich profile attributes.
 * For MVP: username-only authentication, no passwords.
 */
class UserRepository {
    private val usePostgreSQL = DatabaseFactory.isPostgreSQL

    /**
     * Inserts a new user into the database.
     *
     * @param user The user to insert
     */
    fun insertUser(user: User) {
        if (!usePostgreSQL) {
            throw UnsupportedOperationException("SQLite not supported for users in MVP")
        }

        val connection: Connection = DatabaseFactory.getJdbcConnection()
        val sql = """
            INSERT INTO users (
                username, display_name, role, bio, age, gender, has_pets, pet_types,
                sustainability_score, green_title, goodwill_points, created_at
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::timestamp)
        """.trimIndent()

        connection.prepareStatement(sql).use { stmt ->
            stmt.setString(1, user.username)
            stmt.setString(2, user.displayName)
            stmt.setString(3, user.role.name)
            stmt.setString(4, user.bio)
            val age = user.age
            if (age != null) {
                stmt.setInt(5, age)
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER)
            }
            val gender = user.gender
            if (gender != null) {
                stmt.setString(6, gender.name)
            } else {
                stmt.setNull(6, java.sql.Types.VARCHAR)
            }
            stmt.setBoolean(7, user.hasPets)

            // Convert pet types list to PostgreSQL array
            val petTypesArray = connection.createArrayOf("text", user.petTypes.toTypedArray())
            stmt.setArray(8, petTypesArray)

            stmt.setInt(9, user.sustainabilityScore)
            stmt.setString(10, user.greenTitle.name)
            stmt.setInt(11, user.goodwillPoints)
            stmt.setString(12, user.createdAt ?: Instant.now().toString())

            stmt.executeUpdate()
        }
    }

    /**
     * Retrieves a user by username.
     *
     * @param username The username to search for
     * @return The user if found, null otherwise
     */
    fun getUserByUsername(username: String): User? {
        if (!usePostgreSQL) {
            throw UnsupportedOperationException("SQLite not supported for users in MVP")
        }

        val connection: Connection = DatabaseFactory.getJdbcConnection()
        val sql = """
            SELECT username, display_name, role, bio, age, gender, has_pets, pet_types,
                   sustainability_score, green_title, goodwill_points, created_at
            FROM users
            WHERE username = ?
        """.trimIndent()

        connection.prepareStatement(sql).use { stmt ->
            stmt.setString(1, username)
            val rs = stmt.executeQuery()
            return if (rs.next()) resultSetToUser(rs) else null
        }
    }

    /**
     * Retrieves all users.
     *
     * @return List of all users
     */
    fun getAllUsers(): List<User> {
        if (!usePostgreSQL) {
            throw UnsupportedOperationException("SQLite not supported for users in MVP")
        }

        val connection: Connection = DatabaseFactory.getJdbcConnection()
        val sql = """
            SELECT username, display_name, role, bio, age, gender, has_pets, pet_types,
                   sustainability_score, green_title, goodwill_points, created_at
            FROM users
            ORDER BY created_at DESC
        """.trimIndent()

        val users = mutableListOf<User>()
        connection.prepareStatement(sql).use { stmt ->
            val rs = stmt.executeQuery()
            while (rs.next()) {
                users.add(resultSetToUser(rs))
            }
        }
        return users
    }

    /**
     * Updates a user's sustainability score and recalculates their green title.
     *
     * @param username The username
     * @param points Points to add to sustainability score
     */
    fun addSustainabilityPoints(username: String, points: Int) {
        if (!usePostgreSQL) {
            throw UnsupportedOperationException("SQLite not supported for users in MVP")
        }

        val connection: Connection = DatabaseFactory.getJdbcConnection()

        // Update score and recalculate title
        val sql = """
            UPDATE users
            SET sustainability_score = sustainability_score + ?,
                green_title = CASE
                    WHEN sustainability_score + ? >= 1000 THEN 'PLANET_CHAMPION'
                    WHEN sustainability_score + ? >= 500 THEN 'SUSTAINABILITY_HERO'
                    WHEN sustainability_score + ? >= 250 THEN 'GREEN_WARRIOR'
                    WHEN sustainability_score + ? >= 100 THEN 'ECO_CONSCIOUS'
                    ELSE 'BEGINNER'
                END,
                updated_at = CURRENT_TIMESTAMP
            WHERE username = ?
        """.trimIndent()

        connection.prepareStatement(sql).use { stmt ->
            stmt.setInt(1, points)
            stmt.setInt(2, points)
            stmt.setInt(3, points)
            stmt.setInt(4, points)
            stmt.setInt(5, points)
            stmt.setString(6, username)
            stmt.executeUpdate()
        }
    }

    /**
     * Updates a user's goodwill points.
     *
     * @param username The username
     * @param points Points to add to goodwill score
     */
    fun addGoodwillPoints(username: String, points: Int) {
        if (!usePostgreSQL) {
            throw UnsupportedOperationException("SQLite not supported for users in MVP")
        }

        val connection: Connection = DatabaseFactory.getJdbcConnection()
        val sql = """
            UPDATE users
            SET goodwill_points = goodwill_points + ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE username = ?
        """.trimIndent()

        connection.prepareStatement(sql).use { stmt ->
            stmt.setInt(1, points)
            stmt.setString(2, username)
            stmt.executeUpdate()
        }
    }

    /**
     * Checks if a username already exists.
     *
     * @param username The username to check
     * @return true if username exists, false otherwise
     */
    fun userExists(username: String): Boolean {
        if (!usePostgreSQL) {
            throw UnsupportedOperationException("SQLite not supported for users in MVP")
        }

        val connection: Connection = DatabaseFactory.getJdbcConnection()
        val sql = "SELECT COUNT(*) FROM users WHERE username = ?"

        connection.prepareStatement(sql).use { stmt ->
            stmt.setString(1, username)
            val rs = stmt.executeQuery()
            return rs.next() && rs.getInt(1) > 0
        }
    }

    /**
     * Converts a ResultSet row to a User object.
     */
    private fun resultSetToUser(rs: ResultSet): User {
        val petTypesArray = rs.getArray("pet_types")
        val petTypesList = if (petTypesArray != null) {
            (petTypesArray.array as Array<*>).filterNotNull().map { it.toString() }
        } else {
            emptyList()
        }

        val genderString = rs.getString("gender")
        val gender = if (genderString != null) Gender.valueOf(genderString) else null

        val ageInt = rs.getInt("age")
        val age = if (rs.wasNull()) null else ageInt

        return User(
            username = rs.getString("username"),
            displayName = rs.getString("display_name"),
            role = UserRole.valueOf(rs.getString("role")),
            age = age,
            gender = gender,
            hasPets = rs.getBoolean("has_pets"),
            petTypes = petTypesList,
            sustainabilityScore = rs.getInt("sustainability_score"),
            greenTitle = GreenTitle.valueOf(rs.getString("green_title")),
            goodwillPoints = rs.getInt("goodwill_points"),
            bio = rs.getString("bio"),
            createdAt = rs.getString("created_at")
        )
    }
}
