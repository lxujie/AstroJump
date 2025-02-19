// File: GameCanvas.kt
package astrojump

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlinx.coroutines.delay
import astrojump.model.Sprite
import astrojump.util.loadImageFromAssets
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale

@Composable
fun GameCanvas() {
    // Load the AstroBoy image once from assets.
    val astroBoyImage = loadImageFromAssets("AstroBoy.png")

    // Maintain a list of sprites. This state will hold your game objects.
    val sprites = remember { mutableStateListOf<Sprite>() }

    // Initialize the sprite list once the image is loaded.
    LaunchedEffect(astroBoyImage) {
        if (astroBoyImage != null && sprites.isEmpty()) {
            sprites.add(Sprite(image = astroBoyImage, position = mutableStateOf(Offset(100f, 100f))))
        }
    }

    // A simple game loop that updates your sprites.
    LaunchedEffect(Unit) {
        val frameTime = 16L // ~60fps
        while (true) {
            sprites.forEach { sprite ->
                sprite.update(frameTime)
            }
            delay(frameTime)
        }
    }

    // Render the sprites on a Canvas.
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (sprites.isEmpty()) {
            Text(text = "Loading game assets...")
        } else {
            Canvas(modifier = Modifier.fillMaxSize()) {
                sprites.forEach { sprite ->
                    // Use transformations if you want to rotate or scale.
                    withTransform({
                        translate(sprite.position.value.x, sprite.position.value.y)
                        rotate(degrees = sprite.rotation.value)
                        scale(scale = sprite.scale.value)
                    }) {
                        // Draw the sprite with its top left at (0, 0) since we've already translated.
                        drawImage(image = sprite.image)
                    }
                }
            }
        }
    }
}
