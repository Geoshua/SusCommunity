# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Kotlin Multiplatform project for a community application ("sus_community") with three main targets:
- **Android**: Native Android application
- **Web**: Browser-based application (supports both WebAssembly and JS targets)
- **Server**: Ktor-based backend server

The project uses Compose Multiplatform for UI and shares common code across all platforms.

## Project Structure

The project consists of three modules defined in `settings.gradle.kts`:

1. **`composeApp/`**: The client application containing platform-specific and common UI code
   - `commonMain/`: Shared UI code using Compose Multiplatform
   - `androidMain/`: Android-specific code (MainActivity, etc.)
   - `webMain/`: Web-specific code (JS/Wasm entry points)
   - Targets: Android, JS (browser), Wasm (browser)

2. **`server/`**: Ktor server application
   - Main class: `com.sustech.sus_community.ApplicationKt`
   - Server configuration in `server/src/main/kotlin/com/sustech/sus_community/Application.kt`
   - Routes in `server/src/main/kotlin/models/`
   - Uses Ktor with Netty engine

3. **`shared/`**: Shared business logic and models across all targets (Android, JVM, JS, Wasm)
   - `commonMain/`: Platform-agnostic code
   - Platform-specific implementations for Android, JVM, JS, and Wasm in respective folders
   - Contains shared constants (e.g., `SERVER_PORT` in `Constants.kt`)
   - Uses `expect`/`actual` pattern for platform-specific implementations (see `Platform.kt`)

4. **`database/`**: SQL Delight
   - `serialization/`: kotlinx.serialization
   - `map integration/`: Google Maps API 

## Key User Flows & Features:

1. User Segmentation:

   - NewMuenchers: Newcomers needing help integrating.

   - OldMuenchers: Locals offering help and guidance.

   - Feature: Registration flow to select role and assign "Sustainability Score."

2.  "Gig" & Volunteering Board (Post/Respond):

    - Users post requests with tags: Pet Sitting, Tutoring, Elderly Company, Mowing, Moving Help.

     - Users (Volunteers) accept/attend requests.

3.    Sustainability Map:

      - Visual map showing: Recycling locations, Bike rentals, Sustainable shops, Community events.

4.    Local Reporting (Crowdsourcing):

      - Users capture photos of: Broken public infrastructure, Local news/events.

5.    Carbon Footprint Tracker:

      - Personalized dashboard tracking sustainable actions (gamification).

## Current Implementation Status & Strategy:

- `Phase/`: Scaffolding.

- `Data Flow/`: We need offline-first capability using SQLDelight, syncing with a backend via Ktor.

- `Navigation/`: We need a shared navigation solution (e.g., Jetpack Navigation for Compose or Voyager) that works on both Android and Web.

Current To-Dos (Prioritized):

1.    Scaffold Project: Ensure Gradle setup correctly targets Android and Web with a commonMain source set for UI.

2.    Data Layer: Setup SQLDelight schema (.sq) for Users, Requests, and Locations.

3.    Networking: Configure Ktor HTTP Client with kotlinx.serialization for JSON.

4.    UI Skeleton: Create the Main Scaffold (Bottom Navigation) shared across both platforms.
      - top 40% of the page: Map (Sustainability Locations).
      - middle of the page: Create a new post/item us
      - bottom: Feed (Requests).
      - Tab 2: Profile (Carbon Score).


## Build Commands

**Note**: All commands below use `.\gradlew.bat` for Windows. The project is currently being developed on Windows.

### Android Application

Build debug APK:
```bash
.\gradlew.bat :composeApp:assembleDebug
```

### Server

Run server (defaults to port 8080):
```bash
.\gradlew.bat :server:run
```

Run server in development mode with auto-reload:
```bash
.\gradlew.bat :server:run -Pdevelopment
```

### Web Application

**Wasm target** (faster, requires modern browsers):
```bash
.\gradlew.bat :composeApp:wasmJsBrowserDevelopmentRun
```

**JS target** (slower, supports older browsers):
```bash
.\gradlew.bat :composeApp:jsBrowserDevelopmentRun
```

### Testing

Run tests for a specific module:
```bash
.\gradlew.bat :composeApp:test
.\gradlew.bat :server:test
.\gradlew.bat :shared:test
```

Run all tests:
```bash
.\gradlew.bat test
```

### Building

Clean build:
```bash
.\gradlew.bat clean build
```

## Architecture Notes

### Multiplatform Code Sharing

- The `shared` module contains code that is compiled for all targets (Android, JVM for server, JS, Wasm)
- Use `expect`/`actual` declarations for platform-specific implementations while maintaining a common interface
- Example: `Platform.kt` defines an expected interface, with actual implementations in `Platform.android.kt`, `Platform.jvm.kt`, `Platform.js.kt`, and `Platform.wasmJs.kt`

### Server Configuration

- Server port is defined in `shared/src/commonMain/kotlin/com/sustech/sus_community/Constants.kt` as `SERVER_PORT` (currently 8080)
- This allows both server and client code to reference the same constant
- Server runs on `0.0.0.0` to accept connections from all network interfaces

### Dependencies

Key dependencies managed in `gradle/libs.versions.toml`:
- Kotlin 2.2.20
- Compose Multiplatform 1.9.1
- Ktor 3.3.1
- Android target SDK 36, min SDK 24

Plugins are centrally managed using version catalogs and applied at the root level.

### Package Structure

All code uses the base package: `com.sustech.sus_community`

When adding new files:
- Use this package as the root
- Follow Kotlin conventions for subpackages
- Server routes are in the `models` subpackage (currently contains `ItemRoutes.kt`)
