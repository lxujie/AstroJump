// File: GameOverScreen.kt
package astrojump.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import astrojump.util.loadImageFromAssets
import astrojump.ui.theme.rememberCustomFont
import kotlinx.coroutines.delay
import androidx.compose.animation.core.tween
//import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import astrojump.util.ButtonWithImage

@Composable
fun GameOverScreen(navController: NavHostController, score: Int) {
    // Load assets
    val backgroundImage = loadImageFromAssets("Space.png")?.let { BitmapPainter(it) }
    val astroboyImage = loadImageFromAssets("astrodeath2.png")?.let { BitmapPainter(it) }
    val buttonImage = loadImageFromAssets("Button.png")?.let { BitmapPainter(it) }

    // State for the vertical position of AstroBoy
    var startAnimation by remember { mutableStateOf(false) }

    // Animate the Y position (float downward)
    val offsetY by animateFloatAsState(
        targetValue = if (startAnimation) 1000f else 0f, // value for how far you want it to go
        animationSpec = tween(durationMillis = 15000) // animation duration
    )

    // Start animation after a short delay when the screen is displayed
    LaunchedEffect(Unit) {
        delay(1000) // Wait for a second before starting the animation
        startAnimation = true
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        backgroundImage?.let {
            Image(
                painter = it,
                contentDescription = "Game Over Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Game Over Text (Centered at the top)
            Text(
                text = "Game Over",
                fontFamily = rememberCustomFont(),
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp) // Adjust spacing
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Display Score Below Game Over Text
            Text(
                text = "Score: $score",
                fontFamily = rememberCustomFont(),
                fontSize = 30.sp, // Slightly smaller than "Game Over"
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Play Again Button
            buttonImage?.let {
                ButtonWithImage(
                    buttonImage = it,
                    text = "Play Again",
                    onClick = { navController.navigate("game") { popUpTo("gameScreen") { inclusive = true } } }
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Back to Main Menu Button
            buttonImage?.let {
                ButtonWithImage(
                    buttonImage = it,
                    text = "Main Menu",
                    onClick = { navController.navigate("mainMenu") { popUpTo("mainMenu") { inclusive = true } } }
                )
            }
        }

        // AstroBoy Image (Rotated and aligned to bottom right corner, floating down)
        astroboyImage?.let {
            Image(
                painter = it,
                contentDescription = "AstroBoy Stranded",
                modifier = Modifier
                    .size(150.dp)
                    .graphicsLayer(rotationZ = 180f) // Rotate 180 degrees
                    .align(Alignment.BottomEnd) // Position at the bottom right corner
                    .offset { IntOffset(0, offsetY.toInt()) } // Apply animated downward offset
                    .padding(16.dp) // Add some padding from the corner
            )
        }
    }
}
