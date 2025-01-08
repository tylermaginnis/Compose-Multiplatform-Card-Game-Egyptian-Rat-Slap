package org.example.project

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LoginScreen(navigateToGameScreen = {
                // Implement navigation to game screen for Android
                val intent = Intent(this@MainActivity, GameActivity::class.java)
                startActivity(intent)
            })
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    LoginScreen(navigateToGameScreen = {})
}