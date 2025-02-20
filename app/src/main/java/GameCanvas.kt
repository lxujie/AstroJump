// File: GameCanvas.kt
package astrojump

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column
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
import astrojump.input.DetectShakeEvent
import astrojump.input.accelerometerSensor
import astrojump.input.rememberFilteredAcceleration
import astrojump.input.rememberHorizontalMovement

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

    //Read the accelerometer
    val (axRaw, ayRaw, azRaw) = accelerometerSensor()
    //filter
    // 5) Apply optional filtering
    //    For debugging, let's keep alpha moderate, no threshold
    val (ax, _, _) = rememberFilteredAcceleration(
        rawX = axRaw,
        rawY = ayRaw,
        rawZ = azRaw,
        alpha = 0.5f,    // mid-level smoothing
        threshold = 0f   // no dead zone for debugging
    )

    //detect a shake to trigger a "jump" etc
    DetectShakeEvent(ax, ayRaw, azRaw){
        if(sprites.isNotEmpty()){
            sprites[0].position.value = Offset(sprites[0].position.value.x, 50f) //some jump
        }
    }

    // A simple game loop that updates your sprites.
    LaunchedEffect(Unit) {

        val sensitivity = 10f
        val friction = 0.95f
        val frameTimeMs = 16L
        val dtSeconds = frameTimeMs / 1000f

        while (true) {
            if (sprites.isNotEmpty()) {
                val sprite = sprites[0]
                val currentVelocityX = sprite.velocity.value.x
                val newVelocityX = (currentVelocityX + (-ax * sensitivity * dtSeconds)) * friction
                sprite.velocity.value = Offset(newVelocityX, sprite.velocity.value.y)

                //update sprite position
                sprite.update(frameTimeMs)
            }
            delay(16L)
        }
    }

    // Render the sprites on a Canvas.
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (sprites.isEmpty()) {
            Text(text = "Loading game assets...")
        } else {
            Canvas(modifier = Modifier.fillMaxSize()) {
                sprites.forEach { sprite ->
                    withTransform({
                        translate(sprite.position.value.x, sprite.position.value.y)
                        rotate(degrees = sprite.rotation.value)
                        scale(scale = sprite.scale.value)
                    }) {
                        drawImage(image = sprite.image)
                    }
                }
            }
        }

        // 10) Debug information: show sensor & sprite position
        Column(modifier = Modifier.align(Alignment.BottomCenter)) {
            Text("axRaw=$axRaw, axFiltered=$ax")
            if (sprites.isNotEmpty()) {
                Text("spriteX=${sprites[0].position.value.x}")
            }
        }
    }
}
