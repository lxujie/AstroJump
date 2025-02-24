package astrojump.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import astrojump.util.loadImageFromAssets
import androidx.compose.ui.graphics.painter.BitmapPainter
import astrojump.ui.theme.rememberCustomFont
import kotlinx.coroutines.delay

@Composable
fun ObjectiveScreen(navController: NavHostController) {
    val backgroundImage = loadImageFromAssets("Space.png")?.let { BitmapPainter(it) }
    val starImage = loadImageFromAssets("Star.png")?.let { BitmapPainter(it) }
    val asteroidImage = loadImageFromAssets("Asteroid.png")?.let { BitmapPainter(it) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { navController.navigate("game") }
    ) {
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
            Row{
                // Phone Image
                starImage?.let {
                    Image(
                        painter = it,
                        contentDescription = "Phone Image",
                        modifier = Modifier
                            .size(80.dp)
                        //.padding(bottom = 12.dp),
                    )
                }
                Spacer(modifier = Modifier.width(100.dp))

                // Asteroid Image
                asteroidImage?.let {
                    Image(
                        painter = it,
                        contentDescription = "Phone Image",
                        modifier = Modifier
                            .size(80.dp)
                            .rotate(-45f)
                        //.padding(bottom = 12.dp),
                    )
                }
            }
            //Spacer(modifier = Modifier.width(10.dp))
            // Title Text "Astroboy"
            Text(
                text = "Collect Stars, Avoid Asteroids!",
                fontFamily = rememberCustomFont(), // Use your custom font here
                fontSize = 20.sp, // Large title font size
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }
    }

    // Timer for 5 seconds
    var timeLeft by remember { mutableIntStateOf(5) }

    // This launches a coroutine that decreases the timer each second.
    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000L) // wait for 1 second
            timeLeft--
        }
        if(timeLeft == 0){
            navController.navigate("objective")
        }

    }
}




