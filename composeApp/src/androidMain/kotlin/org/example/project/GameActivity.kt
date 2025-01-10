package org.example.project

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import org.example.project.GameScreen

class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameScreen(onBackToLogin = {
                // Start the LoginActivity and then finish the GameActivity
                startActivity(Intent(this@GameActivity, MainActivity::class.java))
                finish()
            })
        }
    }
}