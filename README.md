This is a Kotlin Multiplatform project targeting Android, Web, Server.

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
    - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
    - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
      For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
      the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
      Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
      folder is the appropriate location.

* [/server](./server/src/main/kotlin) is for the Ktor server application.

* [/shared](./shared/src) is for the code that will be shared between all targets in the project.
  The most important subfolder is [commonMain](./shared/src/commonMain/kotlin). If preferred, you
  can add code to the platform-specific folders here too.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:

- on macOS/Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

### Build and Run Server

To build and run the development version of the server, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:

- on macOS/Linux
  ```shell
  ./gradlew :server:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :server:run
  ```

### Build and Run Web Application

To build and run the development version of the web app, use the run configuration from the run widget
in your IDE's toolbar or run it directly from the terminal:

- for the Wasm target (faster, modern browsers):
    - on macOS/Linux
      ```shell
      ./gradlew :composeApp:wasmJsBrowserDevelopmentRun
      ```
    - on Windows
      ```shell
      .\gradlew.bat :composeApp:wasmJsBrowserDevelopmentRun
      ```
- for the JS target (slower, supports older browsers):
    - on macOS/Linux
      ```shell
      ./gradlew :composeApp:jsBrowserDevelopmentRun
      ```
    - on Windows
      ```shell
      .\gradlew.bat :composeApp:jsBrowserDevelopmentRun
      ```

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…

We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack

## Inspiration
Modern neighborhoods often feel disconnected — people live close together but don’t actually interact or share resources. At the same time, communities are facing rising sustainability challenges: waste, overconsumption, isolation, and lack of local support systems.
We wanted to create something simple that helps people connect, help each other, and build more sustainable habits together.
##What it does
GOAL: Pushing sustainability as a community
Enforcing sustainability through community activities.
Strengthen the community by helping each other out.

## How we built it
We used Kotlin Multiplatform with Compose Multiplatform to build one shared codebase for both Android and Web.
This includes:
Shared UI using Compose
Shared logic, models, and repositories
Compose for Web for front-end deployment
Compose for Android for the mobile app
Ktor for networking
Lightweight in-memory or file-based storage for prototyping
Interactive Map that marks the locations of user requests, events and sustainable items(bike rentals, recycle stations)
Backend and database: Ktor postgresql
Functional Backend RESTful API for POST, UPDATE and GET user posts. POST and GET users
## Challenges we ran into
Styling differences between web and mobile layouts
Displaying a map via Google API in the web version


## Accomplishments that we're proud of
Having first time experience with Kotlin and Kotlin Multiplatform

## What we learned
Do not use any kind of ad blocker when testing on the web!

##What's next for SusTech


## Built With
Kotlin Multiplatform
Compose Multiplatform (Android + Web)
Ktor
Gradle
Sleep deprivation
Caffeinated drinks
Determination


channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web).
If you face any issues, please report them on [YouTrack](https://youtrack.jetbrains.com/newIssue?project=CMP).
