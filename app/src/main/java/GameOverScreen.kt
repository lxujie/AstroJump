// File: GameOverScreen.kt
package astrojump.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import astrojump.util.loadImageFromAssets
import astrojump.ui.theme.rememberCustomFont

@Composable
fun GameOverScreen(navController: NavHostController) {
    // Load background and button images
    val backgroundImage = loadImageFromAssets("Space.png")?.let { BitmapPainter(it) }
    val buttonImage = loadImageFromAssets("Button.png")?.let { BitmapPainter(it) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        backgroundImage?.let {
            Image(
                painter = it,
                contentDescription = "Space Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Foreground content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Game Over",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = rememberCustomFont()
            )
            Spacer(modifier = Modifier.height(24.dp))
            // Use custom button if the image loaded, otherwise fallback
            if (buttonImage != null) {
                ButtonWithImage(
                    buttonImage = buttonImage,
                    text = "Back to Main Menu",
                    onClick = { navController.navigate("mainMenu") }
                )
            } else {
                Button(onClick = { navController.navigate("mainMenu") }) {
                    Text("Back to Main Menu")
                }
            }
        }
    }
}
