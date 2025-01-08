package org.example.project

import android.os.Bundle
import android.content.Intent  // Add this import
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.example.project.LoginScreen

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // ... existing code ...
            LoginScreen(navigateToGameScreen = {
                // Navigate to the game screen
                startActivity(Intent(this@LoginActivity, GameActivity::class.java))
            })
            // ... existing code ...
        }
    }
}