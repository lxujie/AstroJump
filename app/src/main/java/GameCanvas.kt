// File: GameCanvas.kt
package astrojump

import android.view.OrientationEventListener
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Column
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
import astrojump.input.accelerometerSensor
import astrojump.input.rememberFilteredAcceleration
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import kotlin.math.abs
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavHostController
import astrojump.model.Player
import astrojump.model.SkyItems
import kotlin.random.Random

var items = 10
var count = 0

@Composable
fun GameScreen(navController: NavHostController) {
    GameCanvas()
}

@Composable
fun GameCanvas() {

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() } // Convert to pixels
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() } // Get screen width

    // Load Background Image
    val backgroundImage = loadImageFromAssets("plains.png")
    // Load the AstroBoy image once from assets.
    val astroBoyImage = loadImageFromAssets("AstroBoy1.png")
    // Bad Object
    val fishImage = loadImageFromAssets("fish.png")
    // Good Object
    val sunImage = loadImageFromAssets("sun1.png")
    // Maintain a list of sprites. This state will hold your game objects.
    val sprites = remember { mutableStateListOf<Sprite>() }
    // Maintain a list of sky items. This state will hold your game objects.
    val skyItems = remember { mutableStateListOf<SkyItems>() }

    // Game State Variables
    var playerHealth by remember { mutableIntStateOf(5) }  // Player starts with 5 health
    var playerScore by remember { mutableIntStateOf(0) }   // Start score at 0

    // Create Player Sprite
    LaunchedEffect(astroBoyImage) {
        if (astroBoyImage != null) {
            val player = Player(
                image = astroBoyImage,
                id = mutableIntStateOf(count++),
                position = mutableStateOf(Offset(screenWidthPx / 2f, screenHeightPx - 300f))
            )
            sprites.add(player) // Add player sprite
        }
    }

    // Initialize Falling Objects (Fish & Sun)
    LaunchedEffect(fishImage, sunImage) {
        while (true) { // Infinite loop to continuously spawn objects
            val randomX = Random.nextFloat() * screenWidthPx // Random X position
            val badVelocity = Random.nextFloat() * 3f + 3f // Bad objects fall between 3f and 6f

            fishImage?.let {
                val badObject = SkyItems(it, mutableIntStateOf(count++), mutableStateOf(Offset(randomX, 0f)))
                badObject.setVelocity(0f, badVelocity) // Faster fall
                sprites.add(badObject)
                skyItems.add(badObject)
            }

            val randomX2 = Random.nextFloat() * screenWidthPx // Another random X for sun
            val goodVelocity = Random.nextFloat() * 2f + 1f // Good objects fall between 1f and 3f

            sunImage?.let {
                val goodObject = SkyItems(it, mutableIntStateOf(count++), mutableStateOf(Offset(randomX2, 0f)))
                goodObject.setVelocity(0f, goodVelocity) // Slower fall
                sprites.add(goodObject)
                skyItems.add(goodObject)
            }

            delay(Random.nextLong(1500L, 2500L))
        }
    }

    // Read accelerometer sensor values
    val (axRaw, ayRaw, azRaw) = accelerometerSensor()
    //filter the raw sensor values
    val (axFiltered, _, _) = rememberFilteredAcceleration(
        rawX = axRaw,
        rawY = ayRaw,
        rawZ = azRaw,
        alpha = 0.5f, // moderate smoothing
        threshold = 0f //no dead zone for debugging
    )

    //always get the latest axFiltered and axRaw value
    val latestAx by rememberUpdatedState(newValue =  axFiltered)
    val latestAxRaw by rememberUpdatedState(newValue = axRaw)

    // Game loop that updates sprites and integrates sensor input
    LaunchedEffect(Unit) {
        val frameTimeMs = 16L // ~60 FPS
        val dt = frameTimeMs / 1000f
        val sensitivity = 200f
        val friction = 0.9f

        while (true) {
            val player = sprites.firstOrNull { it is Player } as? Player

            // Move Player with Accelerometer
            player?.let {
                if (abs(latestAxRaw) < 0.01f) {
                    it.velocity.value = Offset.Zero
                } else {
                    val accelerationX = -latestAx * sensitivity
                    val newVelX = it.velocity.value.x + accelerationX * dt
                    it.velocity.value = Offset(newVelX * friction, it.velocity.value.y)
                }
            }

            // Update Positions and Remove Objects if Needed
            val collidedObjects = mutableListOf<Sprite>()
            val outOfBoundsObjects = mutableListOf<Sprite>()

            sprites.forEach { sprite ->
                sprite.update(dt, screenWidthPx, screenHeightPx)

                // Remove if object falls off the screen
                if (sprite.position.value.y >= screenHeightPx) {
                    outOfBoundsObjects.add(sprite)
                }

                // Check Collision with Player
                if (player != null && sprite is SkyItems && player.checkCollision(sprite)) {
                    if (sprite.image == fishImage) {
                        playerHealth = (playerHealth - 1).coerceAtLeast(0) // Decrease health
                    } else if (sprite.image == sunImage) {
                        playerScore += 100 // Increase score
                    }
                    collidedObjects.add(sprite) // Mark for removal
                }
            }

            // Remove objects marked for deletion
            sprites.removeAll(collidedObjects + outOfBoundsObjects)
            skyItems.removeAll((collidedObjects + outOfBoundsObjects).toSet())

            delay(frameTimeMs)
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

            /*
            // Display Health & Score
            Column(modifier = Modifier.align(Alignment.TopCenter)) {
                Text(text = "Health: $playerHealth", color = Color.Red)
                Text(text = "Score: $playerScore", color = Color.Yellow)
            }
             */

            /*
            Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                Text("axRaw = $axRaw, axFiltered = $axFiltered")
                if (sprites.isNotEmpty()) {
                    Text("Sprite1 velocityX = ${sprites[0].velocity.value.x}")
                    Text("Sprite1 X = ${sprites[0].position.value.x}")
                }
                //Text("ayRaw = $ayRaw, azRaw = $azRaw")
             */
        }
    }
}