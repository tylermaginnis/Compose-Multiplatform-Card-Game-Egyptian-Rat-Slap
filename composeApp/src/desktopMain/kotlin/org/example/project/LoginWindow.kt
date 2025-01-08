package org.example.project

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "KotlinProject"
    ) {
        var showLoginScreen by remember { mutableStateOf(true) }
        
        if (showLoginScreen) {
            LoginScreen(navigateToGameScreen = {
                showLoginScreen = false
            })
        } else {
            GameScreen(onBackToLogin = {
                showLoginScreen = true
            })
        }
    }
}