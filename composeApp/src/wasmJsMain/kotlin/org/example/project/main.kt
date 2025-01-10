package org.example.project

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.browser.window
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
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