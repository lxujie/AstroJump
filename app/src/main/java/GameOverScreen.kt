package astrojump.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun GameOverScreen(navController: NavHostController) {
    Log.d("GameDebug", "GameOverScreen Composed")
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Game Over")
        Button(onClick = { navController.navigate("mainMenu") }) {
            Text("Back to Main Menu")
        }
    }
}