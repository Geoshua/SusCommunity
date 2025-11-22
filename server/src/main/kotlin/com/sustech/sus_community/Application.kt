package com.sustech.sus_community

import com.sustech.sus_community.database.DatabaseFactory
import com.sustech.sus_community.routes.postRoutes
import com.sustech.sus_community.routes.userRoutes
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

/**
 * Main entry point for the Ktor server application.
 *
 * Initializes the SQLDelight database and starts an embedded Netty server
 * on the port defined in SERVER_PORT (default: 8081).
 * The server listens on all network interfaces (0.0.0.0) to accept connections from
 * both localhost and external clients (e.g., Android emulator, physical devices).
 */
fun main() {
    // Initialize the database
    DatabaseFactory.init()

    // Start the server
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

/**
 * Configures the Ktor application with necessary plugins and routing.
 *
 * Installed plugins:
 * - ContentNegotiation: Enables automatic JSON serialization/deserialization using kotlinx.serialization
 *
 * Configured routes:
 * - GET / : Health check endpoint
 * - /posts : Post/request management endpoints (see PostRoutes.kt)
 */
fun Application.module() {
    // Install ContentNegotiation plugin for JSON serialization
    install(ContentNegotiation) {
        json(Json {
            // Pretty print JSON in responses for easier debugging
            prettyPrint = true
            // Don't fail on unknown properties (allows API evolution)
            ignoreUnknownKeys = true
        })
    }

    // Configure routing
    routing {
        // Health check endpoint
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }

        // User management endpoints
        userRoutes()

        // Post/request management endpoints
        postRoutes()
    }
}