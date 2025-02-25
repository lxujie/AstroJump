package astrojump.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun HowToPlayScreen(navController: NavHostController) {
    val backgroundImage = loadImageFromAssets("Space.png")?.let { BitmapPainter(it) }
    val phoneImage = loadImageFromAssets("Phone.png")?.let { BitmapPainter(it) }

    // A flag to ensure we only navigate once.
    var hasNavigated by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                if (!hasNavigated) {
                    hasNavigated = true
                    navController.navigate("objective")
                }
            }
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
            // Phone Image
            phoneImage?.let {
                Image(
                    painter = it,
                    contentDescription = "Phone Image",
                    modifier = Modifier.size(160.dp)
                )
            }
            // Title Text "Tilt phone to move"
            Text(
                text = "Tilt phone to move",
                fontFamily = rememberCustomFont(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Text(
            text = "Tap anywhere to skip",
            fontFamily = rememberCustomFont(), // Use your custom font here
            fontSize = 16.sp, // Large title font size
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp),

            )
    }

    // Timer for 5 seconds
    var timeLeft by remember { mutableIntStateOf(5) }
    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000L) // wait for 1 second
            timeLeft--
        }
        if (timeLeft == 0 && !hasNavigated) {
            hasNavigated = true
            navController.navigate("objective")
        }
    }
}
