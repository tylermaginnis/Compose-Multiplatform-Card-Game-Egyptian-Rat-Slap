package org.example.project

import androidx.compose.ui.window.ComposeUIViewController

fun GameViewController() = ComposeUIViewController {
    GameScreen(onBackToLogin = {
        // Implement navigation back to login screen for iOS
        // This might involve using SwiftUI navigation or custom logic
    })
}