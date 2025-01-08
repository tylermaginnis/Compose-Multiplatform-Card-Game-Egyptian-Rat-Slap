package org.example.project

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.example.project.LoginScreen

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "KotlinProject"
    ) {
        var showLoginScreen by remember { mutableStateOf(true) }

        LoginScreen(
            navigateToGameScreen = {
                showLoginScreen = false
            },
            onBackToLogin = {
                showLoginScreen = true
            }
        )
    }
}