// File: MainActivity.kt
package astrojump

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import astrojump.ui.GameOverScreen
import astrojump.ui.MainMenuScreen
import astrojump.ui.SettingsScreen
import astrojump.ui.ObjectiveScreen
import astrojump.ui.HowToPlayScreen

import android.content.pm.ActivityInfo

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // or SCREEN_ORIENTATION_LANDSCAPE

        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "mainMenu") {
        composable("mainMenu") { MainMenuScreen(navController) }
        composable("settings") { SettingsScreen(navController) }
        composable("howToPlay") { HowToPlayScreen(navController) }
        composable("objective") { ObjectiveScreen(navController) }
        composable("game") { GameScreen(navController) }
        composable("gameOver") { GameOverScreen(navController) }
    }
}
