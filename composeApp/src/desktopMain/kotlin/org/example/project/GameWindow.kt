package org.example.project

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "KotlinProject - Game"
    ) {
        GameScreen(onBackToLogin = {
            // Implement navigation back to login screen for Desktop
            // This might involve opening a new window or replacing the current content
        })
    }
}