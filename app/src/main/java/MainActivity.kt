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
import astrojump.ui.ObjectiveScreen
import astrojump.ui.HowToPlayScreen

import android.content.pm.ActivityInfo
import androidx.navigation.NavType
import androidx.navigation.navArgument
import astrojump.ui.GameHistory

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
        composable("gameHistory") { GameHistory(navController) }
        composable("howToPlay") { HowToPlayScreen(navController) }
        composable("objective") { ObjectiveScreen(navController) }
        composable("game") { GameScreen(navController) }
        composable(
            route = "gameOver/{score}",
            arguments = listOf(
                navArgument("score") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            // Extract the score from navigation arguments
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            GameOverScreen(navController, score)
        }
    }
}
