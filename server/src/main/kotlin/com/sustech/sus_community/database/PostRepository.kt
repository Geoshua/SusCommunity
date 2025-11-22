package com.sustech.sus_community.database

import com.sustech.sus_community.models.Location
import com.sustech.sus_community.models.Post
import com.sustech.sus_community.models.PostStatus
import com.sustech.sus_community.models.PostTag
import com.sustech.suscommunity.db.Post as DbPost
import java.sql.ResultSet
import java.sql.Connection
import java.util.UUID

/**
 * Repository for managing Post data in the database.
 *
 * This class provides a clean API for CRUD operations on posts,
 * handling the conversion between database models and domain models.
 * Supports both SQLite (dev) and PostgreSQL (prod) schemas.
 */
class PostRepository {
    private val queries = DatabaseFactory.database?.postQueries
    private val usePostgreSQL = DatabaseFactory.isPostgreSQL

    /**
     * Inserts a new post into the database.
     *
     * @param post The post to insert
     */
    fun insertPost(post: Post) {
        if (usePostgreSQL) {
            insertPostPostgreSQL(post)
        } else {
            queries!!.insertPost(
                id = post.id!!,
                title = post.title,
                description = post.description,
                latitude = post.location.latitude,
                longitude = post.location.longitude,
                address = post.location.address,
                tag = post.tag.name,
                dueDate = post.dueDate,
                femaleOnly = if (post.femaleOnly) 1L else 0L,
                images = post.images.joinToString(","),
                authorId = post.authorId!!,
                createdAt = post.createdAt!!,
                status = post.status.name
            )
        }
    }

    /**
     * Retrieves all posts from the database.
     *
     * @return List of all posts, ordered by creation date (newest first)
     */
    fun getAllPosts(): List<Post> {
        return if (usePostgreSQL) {
            getAllPostsPostgreSQL()
        } else {
            queries!!.getAllPosts()
                .executeAsList()
                .map { it.toPost() }
        }
    }

    /**
     * Retrieves a post by its ID.
     *
     * @param id The post ID
     * @return The post if found, null otherwise
     */
    fun getPostById(id: String): Post? {
        return if (usePostgreSQL) {
            getPostByIdPostgreSQL(id)
        } else {
            queries!!.getPostById(id)
                .executeAsOneOrNull()
                ?.toPost()
        }
    }

    /**
     * Retrieves posts filtered by tag.
     *
     * @param tag The post tag to filter by
     * @return List of posts with the specified tag
     */
    fun getPostsByTag(tag: PostTag): List<Post> {
        return queries!!.getPostsByTag(tag.name)
            .executeAsList()
            .map { it.toPost() }
    }

    /**
     * Retrieves posts filtered by status.
     *
     * @param status The post status to filter by
     * @return List of posts with the specified status
     */
    fun getPostsByStatus(status: PostStatus): List<Post> {
        return queries!!.getPostsByStatus(status.name)
            .executeAsList()
            .map { it.toPost() }
    }

    /**
     * Deletes a post by its ID.
     *
     * @param id The post ID
     */
    fun deletePostById(id: String) {
        if (usePostgreSQL) {
            deletePostPostgreSQL(id)
        } else {
            queries!!.deletePostById(id)
        }
    }

    /**
     * Updates an entire post.
     *
     * @param id The post ID
     * @param post The updated post data
     */
    fun updatePost(id: String, post: Post) {
        if (usePostgreSQL) {
            updatePostPostgreSQL(id, post)
        } else {
            // For SQLite, we'll need to add an update query in the .sq file
            // For now, delete and re-insert
            queries!!.deletePostById(id)
            queries.insertPost(
                id = id,
                title = post.title,
                description = post.description,
                latitude = post.location.latitude,
                longitude = post.location.longitude,
                address = post.location.address,
                tag = post.tag.name,
                dueDate = post.dueDate,
                femaleOnly = if (post.femaleOnly) 1L else 0L,
                images = post.images.joinToString(","),
                authorId = post.authorId!!,
                createdAt = post.createdAt!!,
                status = post.status.name
            )
        }
    }

    /**
     * Updates the status of a post.
     *
     * @param id The post ID
     * @param status The new status
     */
    fun updatePostStatus(id: String, status: PostStatus) {
        if (usePostgreSQL) {
            val connection: java.sql.Connection = DatabaseFactory.getJdbcConnection()
            val sql = "UPDATE posts SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?::uuid"
            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, status.name)
                stmt.setString(2, id)
                stmt.executeUpdate()
            }
        } else {
            queries!!.updatePostStatus(status.name, id)
        }
    }

    // ========== PostgreSQL-specific methods ==========

    private fun insertPostPostgreSQL(post: Post) {
        val connection: java.sql.Connection = DatabaseFactory.getJdbcConnection()
        val sql = """
            INSERT INTO posts (id, author_id, title, description, tag, location, address, due_date, female_only, status, created_at)
            VALUES (?::uuid, ?::uuid, ?, ?, ?, ST_SetSRID(ST_MakePoint(?, ?), 4326), ?, ?::timestamp, ?, ?, ?::timestamp)
        """.trimIndent()

        connection.prepareStatement(sql).use { stmt ->
            stmt.setString(1, post.id!!)
            stmt.setString(2, post.authorId!!)
            stmt.setString(3, post.title)
            stmt.setString(4, post.description)
            stmt.setString(5, post.tag.name)
            stmt.setDouble(6, post.location.longitude)  // PostGIS uses (longitude, latitude)
            stmt.setDouble(7, post.location.latitude)
            stmt.setString(8, post.location.address)
            stmt.setString(9, post.dueDate)
            stmt.setBoolean(10, post.femaleOnly)
            stmt.setString(11, post.status.name)
            stmt.setString(12, post.createdAt!!)
            stmt.executeUpdate()
        }

        // Insert images if any
        if (post.images.isNotEmpty()) {
            val imageSql = "INSERT INTO post_images (post_id, image_url, display_order) VALUES (?::uuid, ?, ?)"
            connection.prepareStatement(imageSql).use { stmt ->
                post.images.forEachIndexed { index, imageUrl ->
                    stmt.setString(1, post.id)
                    stmt.setString(2, imageUrl)
                    stmt.setInt(3, index)
                    stmt.executeUpdate()
                }
            }
        }
    }

    private fun getAllPostsPostgreSQL(): List<Post> {
        val connection: java.sql.Connection = DatabaseFactory.getJdbcConnection()
        val sql = """
            SELECT
                p.id, p.author_id, p.title, p.description, p.tag,
                ST_X(p.location::geometry) as longitude,
                ST_Y(p.location::geometry) as latitude,
                p.address, p.due_date, p.female_only, p.status, p.created_at,
                COALESCE(array_agg(pi.image_url ORDER BY pi.display_order) FILTER (WHERE pi.image_url IS NOT NULL), '{}') as images
            FROM posts p
            LEFT JOIN post_images pi ON p.id = pi.post_id
            GROUP BY p.id, p.author_id, p.title, p.description, p.tag, p.location, p.address, p.due_date, p.female_only, p.status, p.created_at
            ORDER BY p.created_at DESC
        """.trimIndent()

        val posts = mutableListOf<Post>()
        connection.prepareStatement(sql).use { stmt ->
            val rs = stmt.executeQuery()
            while (rs.next()) {
                posts.add(resultSetToPost(rs))
            }
        }
        return posts
    }

    private fun getPostByIdPostgreSQL(id: String): Post? {
        val connection: java.sql.Connection = DatabaseFactory.getJdbcConnection()
        val sql = """
            SELECT
                p.id, p.author_id, p.title, p.description, p.tag,
                ST_X(p.location::geometry) as longitude,
                ST_Y(p.location::geometry) as latitude,
                p.address, p.due_date, p.female_only, p.status, p.created_at,
                COALESCE(array_agg(pi.image_url ORDER BY pi.display_order) FILTER (WHERE pi.image_url IS NOT NULL), '{}') as images
            FROM posts p
            LEFT JOIN post_images pi ON p.id = pi.post_id
            WHERE p.id = ?::uuid
            GROUP BY p.id, p.author_id, p.title, p.description, p.tag, p.location, p.address, p.due_date, p.female_only, p.status, p.created_at
        """.trimIndent()

        connection.prepareStatement(sql).use { stmt ->
            stmt.setString(1, id)
            val rs = stmt.executeQuery()
            return if (rs.next()) resultSetToPost(rs) else null
        }
    }

    private fun resultSetToPost(rs: ResultSet): Post {
        val imagesArray = rs.getArray("images")
        val imagesList = if (imagesArray != null) {
            (imagesArray.array as Array<*>).filterNotNull().map { it.toString() }
        } else {
            emptyList()
        }

        return Post(
            id = rs.getString("id"),
            title = rs.getString("title"),
            description = rs.getString("description"),
            location = Location(
                latitude = rs.getDouble("latitude"),
                longitude = rs.getDouble("longitude"),
                address = rs.getString("address")
            ),
            tag = PostTag.valueOf(rs.getString("tag")),
            dueDate = rs.getString("due_date"),
            femaleOnly = rs.getBoolean("female_only"),
            images = imagesList,
            authorId = rs.getString("author_id"),
            createdAt = rs.getString("created_at"),
            status = PostStatus.valueOf(rs.getString("status"))
        )
    }

    private fun deletePostPostgreSQL(id: String) {
        val connection: java.sql.Connection = DatabaseFactory.getJdbcConnection()

        // Delete related images first (CASCADE should handle this, but let's be explicit)
        val deleteImagesSql = "DELETE FROM post_images WHERE post_id = ?::uuid"
        connection.prepareStatement(deleteImagesSql).use { stmt ->
            stmt.setString(1, id)
            stmt.executeUpdate()
        }

        // Delete the post
        val deletePostSql = "DELETE FROM posts WHERE id = ?::uuid"
        connection.prepareStatement(deletePostSql).use { stmt ->
            stmt.setString(1, id)
            stmt.executeUpdate()
        }
    }

    private fun updatePostPostgreSQL(id: String, post: Post) {
        val connection: java.sql.Connection = DatabaseFactory.getJdbcConnection()

        // Update the post
        val sql = """
            UPDATE posts
            SET title = ?,
                description = ?,
                tag = ?,
                location = ST_SetSRID(ST_MakePoint(?, ?), 4326),
                address = ?,
                due_date = ?::timestamp,
                female_only = ?,
                status = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = ?::uuid
        """.trimIndent()

        connection.prepareStatement(sql).use { stmt ->
            stmt.setString(1, post.title)
            stmt.setString(2, post.description)
            stmt.setString(3, post.tag.name)
            stmt.setDouble(4, post.location.longitude)
            stmt.setDouble(5, post.location.latitude)
            stmt.setString(6, post.location.address)
            stmt.setString(7, post.dueDate)
            stmt.setBoolean(8, post.femaleOnly)
            stmt.setString(9, post.status.name)
            stmt.setString(10, id)
            stmt.executeUpdate()
        }

        // Update images: delete old ones and insert new ones
        val deleteImagesSql = "DELETE FROM post_images WHERE post_id = ?::uuid"
        connection.prepareStatement(deleteImagesSql).use { stmt ->
            stmt.setString(1, id)
            stmt.executeUpdate()
        }

        if (post.images.isNotEmpty()) {
            val insertImageSql = "INSERT INTO post_images (post_id, image_url, display_order) VALUES (?::uuid, ?, ?)"
            connection.prepareStatement(insertImageSql).use { stmt ->
                post.images.forEachIndexed { index, imageUrl ->
                    stmt.setString(1, id)
                    stmt.setString(2, imageUrl)
                    stmt.setInt(3, index)
                    stmt.executeUpdate()
                }
            }
        }
    }

    /**
     * Converts a database Post model to a domain Post model (SQLite).
     */
    private fun DbPost.toPost(): Post {
        return Post(
            id = id,
            title = title,
            description = description,
            location = Location(
                latitude = latitude,
                longitude = longitude,
                address = address
            ),
            tag = PostTag.valueOf(tag),
            dueDate = dueDate,
            femaleOnly = femaleOnly != 0L,  // Convert Long to Boolean
            images = if (images.isBlank()) emptyList() else images.split(","),
            authorId = authorId,
            createdAt = createdAt,
            status = PostStatus.valueOf(status)
        )
    }
}
