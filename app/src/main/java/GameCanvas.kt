// File: GameCanvas.kt
package astrojump

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration

var items = 10;
@Composable
fun GameCanvas() {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() } // Convert to pixels

    // Load Background Image
    val backgroundImage = loadImageFromAssets("plains.png")
    // Load the AstroBoy image once from assets.
    val astroBoyImage = loadImageFromAssets("AstroBoy1.png")
    val FishImage = loadImageFromAssets("fish.png")

    // Maintain a list of sprites. This state will hold your game objects.
    val sprites = remember { mutableStateListOf<Sprite>() }

    // Initialize the sprite list once the image is loaded.
    LaunchedEffect(astroBoyImage) {

        if (astroBoyImage != null) {
            // Create first sprite moving right
            //val sprite1 = Sprite(image = astroBoyImage, position = mutableStateOf(Offset(100f, 100f)))
            //sprite1.setVelocity(1f, 0f) // Move right

            // Create second sprite moving left
            val sprite2 = Sprite(image = astroBoyImage, position = mutableStateOf(Offset(600f, 2000f)))
            //sprite2.setVelocity(-1f, 0f) // Move left

            // Add both sprites
            sprites.addAll(listOf(sprite2))
        }
    }
    LaunchedEffect(FishImage) {
        for(i in 1..items){
            if (FishImage != null) {
                val sprite = Sprite(image = FishImage,
                    position = mutableStateOf(Offset(i *100f, 100f))
                )
                sprite.setVelocity(0f, 1f) // Move right
                sprites.addAll(listOf(sprite))
            }
            else{
                println("Fish Image is null")
            }
        }
    }

    // A simple game loop that updates your sprites.
    LaunchedEffect(Unit) {
        val frameTime = 16L // ~60fps
        while (true) {
            sprites.forEach { sprite ->
                sprite.update(frameTime)

                // Debug print when sprite collides with the floor
                if (sprite.checkFloorCollision(screenHeightPx)) {
                    //println("Sprite hit the floor!")
                }
            }

            // Do things if got collision
            var collisionDetected = false
            for (i in sprites.indices) {
                for (j in i + 1 until sprites.size) {
                    if (sprites[i].checkCollision(sprites[j])) {
                        sprites[i].color.value = Color.Red
                        sprites[j].color.value = Color.Red
                        collisionDetected = true
                        println("Collision detected between Sprite $i and Sprite $j")
                    }
                }
            }

            // Do things if no collision
            if (!collisionDetected) {
                sprites.forEach { it.color.value = Color.White }
                //println("Collision not detected")
            }

            delay(frameTime)
        }
    }

    // Render the sprites on a Canvas.
    Box(modifier = Modifier.fillMaxSize().background(Color.Blue), contentAlignment = Alignment.Center) {
        if (sprites.isEmpty()) {
            Text(text = "Loading game assets...")
        } else {
            if (backgroundImage != null) {
                Image(
                    bitmap = backgroundImage,
                    contentDescription = "Background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
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

                    // Draw the AABB Bounding Box
                    drawRect(
                        color = Color.Green, // Box color
                        topLeft = Offset(sprite.boundingBox.left, sprite.boundingBox.top),
                        size = androidx.compose.ui.geometry.Size(
                            width = sprite.boundingBox.width,
                            height = sprite.boundingBox.height
                        ),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f) // Outline stroke
                    )
                }
            }
        }
    }
}
