package org.example.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asComposePaint
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoginScreen(
    navigateToGameScreen: () -> Unit,
    onBackToLogin: () -> Unit = {}
) {
    val subtitle = remember { mutableStateOf("Welcome to the game!") }
    var showLogin by remember { mutableStateOf(true) }

    // Define the gradient colors
    val gradientColors = listOf(
        Color(0xFFE0BBE4), // Light purple
        Color(0xFF957DAD)  // Darker purple
    )

    // Use a more flexible layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(colors = gradientColors))
            .drawWithContent {
                drawContent()
                // Add a subtle noise effect
                drawRect(brush = Brush.linearGradient(
                    colors = listOf(Color.Black.copy(alpha = 0.05f), Color.Black.copy(alpha = 0.05f))
                ))
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = showLogin,
                enter = slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }),
                exit = slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth })
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Egyptian Rat Screw",
                        style = MaterialTheme.typography.h4,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = subtitle.value,
                        style = MaterialTheme.typography.subtitle1,
                        textAlign = TextAlign.Center
                    )
                    Button(onClick = { /* Handle Sign Up */ }) {
                        Text("Sign Up")
                    }
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
}