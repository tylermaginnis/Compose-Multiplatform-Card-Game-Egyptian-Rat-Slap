# Kotlin Multiplatform Project

This is a Kotlin Multiplatform project targeting Android, iOS, Web, and Desktop platforms. It utilizes Compose Multiplatform for shared UI components across all targets.

## Project Structure

- `/composeApp`: Shared code for Compose Multiplatform applications
  - `commonMain`: Code common to all targets
  - Platform-specific folders (e.g., `androidMain`, `iosMain`, `wasmJsMain`, `desktopMain`): Code compiled for specific platforms
- `/iosApp`: iOS-specific code and entry point

## Functionality

### Shared Components

The project includes shared components that can be used across all platforms:

- `App.kt`: Main application composable with a button to toggle content visibility
- `LoginScreen.kt`: Login screen with animated visibility and a noise effect

### Platform-Specific Implementations

Each platform has its own entry point and specific implementations:

- **Android**:
  - `MainActivity.kt`: Entry point for Android app, displays the login screen
  - `AndroidLogger.kt`: Android-specific logging implementation

- **iOS**:
  - `iOSApp.swift`: Entry point for iOS app, displays the `ContentView`
  - `ContentView.swift`: Wraps the Compose UI in a SwiftUI view
  - `iosLogger.kt`: iOS-specific logging implementation

- **Web**:
  - `main.kt`: Entry point for web app, displays the login screen
  - `LoginWeb.kt`: Web-specific implementation of the login screen
  - `GameWeb.kt`: Web-specific implementation of the game screen
  - `WebLogger.kt`: Web-specific logging implementation

- **Desktop**:
  - `main.kt`: Entry point for desktop app, displays the login screen
  - `DesktopLogger.kt`: Desktop-specific logging implementation

### Logging

The project implements a common `ILogger` interface with platform-specific implementations for Android, iOS, Web, and Desktop.

### Game Logic

The game implemented in this project is Egyptian Rat Slap. Here's an overview of the game logic:

- **Game State Management**: The game state is managed using a `GameState` object, which tracks the deck, players, current player, and pile.

- **Player Management**: The game supports multiple players, including human and AI players. The current player is tracked based on the game state.

- **Game Flow**: The game flow is controlled by state variables that track player actions, button states, and game messages.

- **Card Play Logic**: Players can play cards from their hand, and the game logic handles the card play, updates the game state, and triggers animations.

- **Slapping Logic**: The game implements a slapping mechanism, where players can attempt to slap the pile under certain conditions. The logic checks for valid slaps and updates the game state accordingly.

- **Game End Condition**: The game checks for a winner based on the game state and displays the winner when the game ends.

- **UI Updates**: The game logic continuously updates the UI to reflect the current game state, including hand sizes, current player, pile size, card rendering, scores, messages, and button states.

- **Animations**: The game uses animations to enhance the user experience, such as card animations and button alpha animations.

- **AI Turn Handling**: The game includes logic to handle AI turns, allowing for a single-player experience against computer opponents.

## Build and Run

- Android: Standard Android build process
- iOS: Xcode project included (`iosApp.xcodeproj`)
- Web: Run `:composeApp:wasmJsBrowserDevelopmentRun` Gradle task
- Desktop: Standard Kotlin/JVM build process

## Learn More

For more information, refer to the official documentation:

- [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
- [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform)
- [Kotlin/Wasm](https://kotl.in/wasm/)

## Feedback and Issues

We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web). If you face any issues, please report them on [GitHub](https://github.com/JetBrains/compose-multiplatform/issues).