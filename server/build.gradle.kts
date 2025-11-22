plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.sqldelight)
    application
}

group = "com.sustech.sus_community"
version = "1.0.0"
application {
    mainClass.set("com.sustech.sus_community.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

tasks.withType<JavaExec>().configureEach {
    if (name == "run") { // This targets the 'run' task
        // Set environment variables for the 'run' task, falling back to defaults if not already set
        environment("DATABASE_URL", System.getenv("DATABASE_URL") ?: "jdbc:postgresql://localhost:5432/suscommunity")
        environment("DATABASE_USER", System.getenv("DATABASE_USER") ?: "suscommunity_user")
        environment("DATABASE_PASSWORD", System.getenv("DATABASE_PASSWORD") ?: "dev_password_2024")
    }
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.sqlite.driver)
    implementation(libs.postgresql.driver)
    implementation(libs.runtime)
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
}

sqldelight {
    databases {
        create("SusCommunityDatabase") {
            packageName.set("com.sustech.suscommunity.db")
        }
    }
}