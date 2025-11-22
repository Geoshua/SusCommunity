package com.sustech.sus_community.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.sustech.suscommunity.db.SusCommunityDatabase
import java.sql.Connection
import java.sql.DriverManager

/**
 * Factory object for creating and managing the database instance.
 *
 * This singleton provides a centralized way to access the database.
 * Supports both SQLite (development) and PostgreSQL (production).
 *
 * Environment Variables:
 * - DATABASE_URL: JDBC connection string (e.g., "jdbc:postgresql://localhost:5432/suscommunity")
 * - DATABASE_USER: Database username (for PostgreSQL)
 * - DATABASE_PASSWORD: Database password (for PostgreSQL)
 */
object DatabaseFactory {
    /**
     * The database driver (SQLite only - used for SQLDelight).
     */
    private var driver: SqlDriver? = null

    /**
     * The main database instance (SQLite only - used for SQLDelight).
     */
    var database: SusCommunityDatabase? = null
        private set

    /**
     * JDBC connection for PostgreSQL (when using PostgreSQL).
     * Null when using SQLite.
     */
    private var jdbcConnection: Connection? = null

    /**
     * Flag to track whether we're using PostgreSQL.
     */
    val isPostgreSQL: Boolean
        get() = jdbcConnection != null

    /**
     * Initializes the database based on environment variables.
     *
     * If DATABASE_URL is set and contains "postgresql", connects to PostgreSQL.
     * Otherwise, uses SQLite with the specified file path.
     *
     * @param sqlitePath Path for SQLite database file (used only if DATABASE_URL is not set).
     */
    fun init(sqlitePath: String = "sus_community.db") {
        val databaseUrl = System.getenv("DATABASE_URL")
        val databaseUser = System.getenv("DATABASE_USER")
        val databasePassword = System.getenv("DATABASE_PASSWORD")

        if (databaseUrl != null && databaseUrl.contains("postgresql")) {
            // Use PostgreSQL (raw JDBC, no SQLDelight)
            println("Connecting to PostgreSQL: $databaseUrl")
            jdbcConnection = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword)
            println("PostgreSQL connection established successfully")
        } else {
            // Use SQLite (with SQLDelight)
            println("Using SQLite database: $sqlitePath")
            val sqliteDriver = JdbcSqliteDriver("jdbc:sqlite:$sqlitePath")
            // Create schema only for SQLite (PostgreSQL schema is created by init scripts)
            SusCommunityDatabase.Schema.create(sqliteDriver)
            driver = sqliteDriver
            database = SusCommunityDatabase(sqliteDriver)
            println("SQLite database initialized successfully")
        }
    }

    /**
     * Gets a raw JDBC connection for PostgreSQL queries.
     * Only available when using PostgreSQL.
     */
    fun getJdbcConnection(): Connection {
        return jdbcConnection ?: throw IllegalStateException("JDBC connection only available for PostgreSQL")
    }

    /**
     * Closes the database connection.
     * Should be called when shutting down the application.
     */
    fun close() {
        driver?.close()
        jdbcConnection?.close()
    }
}
