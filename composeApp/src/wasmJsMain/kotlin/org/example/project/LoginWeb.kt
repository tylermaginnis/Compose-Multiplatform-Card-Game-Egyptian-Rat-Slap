package org.example.project

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        LoginScreen(navigateToGameScreen = {
            // Implement navigation to game screen for Web
            // This might involve changing the content of the ComposeViewport
        })
    }
}