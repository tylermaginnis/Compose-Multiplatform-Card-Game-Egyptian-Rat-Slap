package org.example.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.MaterialTheme

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoginScreen(
    navigateToGameScreen: () -> Unit,
    onBackToLogin: () -> Unit = {}
) {
    val subtitle = remember { mutableStateOf("Welcome to the game!") }
    var showLogin by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = showLogin,
            enter = slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }),
            exit = slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth })
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Egyptian Rat Screw", style = MaterialTheme.typography.h4)
                Spacer(modifier = Modifier.height(16.dp))
                Text(subtitle.value, style = MaterialTheme.typography.subtitle1)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { /* Handle Sign Up */ }) {
                    Text("Sign Up")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    showLogin = false
                    navigateToGameScreen()
                }) {
                    Text("Play as Guest")
                }
            }
        }

        AnimatedVisibility(
            visible = !showLogin,
            enter = slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }),
            exit = slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth })
        ) {
            GameScreen(onBackToLogin = {
                showLogin = true
                onBackToLogin()
            })
        }
    }
}