package org.example.project

import androidx.compose.ui.window.ComposeUIViewController

fun LoginViewController() = ComposeUIViewController {
    LoginScreen(navigateToGameScreen = {
        // Implement navigation to game screen for iOS
        // This might involve changing the root view controller or presenting a new one
    })
}