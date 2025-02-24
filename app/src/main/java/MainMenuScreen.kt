package astrojump.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import astrojump.util.loadImageFromAssets
import androidx.compose.ui.graphics.painter.BitmapPainter
import astrojump.ui.theme.rememberCustomFont
import android.view.SoundEffectConstants
import androidx.compose.ui.platform.LocalView
import astrojump.util.ButtonWithImage

@Composable
fun MainMenuScreen(navController: NavHostController) {
    val backgroundImage = loadImageFromAssets("Space.png")?.let { BitmapPainter(it) }
    val astroboyImage = loadImageFromAssets("AstroBoy1.png")?.let { BitmapPainter(it) }
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Astroboy Mascot and Title in Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {

                //Spacer(modifier = Modifier.width(10.dp))
                // Title Text "Astroboy"
                Text(
                    text = "Astroboy",
                    fontFamily = rememberCustomFont(), // Use your custom font here
                    fontSize = 50.sp, // Large title font size
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )

                // Astroboy Mascot
                astroboyImage?.let {
                    Image(
                        painter = it,
                        contentDescription = "Astroboy Mascot",
                        modifier = Modifier
                            .size(160.dp)
                            .padding(bottom = 36.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            // Start Game Button
            buttonImage?.let {
                ButtonWithImage(
                    buttonImage = it,
                    text = "Start",
                    onClick = { navController.navigate("howToPlay") }
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Settings Button
            buttonImage?.let {
                ButtonWithImage(
                    buttonImage = it,
                    text = "Game History",
                    onClick = { navController.navigate("gameHistory") }
                )
            }
        }
    }
}
