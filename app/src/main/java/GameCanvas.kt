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
    val fishImage = loadImageFromAssets("fish.png")
    // Maintain a list of sprites. This state will hold your game objects.
    val sprites = remember { mutableStateListOf<Sprite>() }
    // Maintain a list of sky items. This state will hold your game objects.
    val skyItems = remember { mutableStateListOf<SkyItems>() }

    // Initialize the sprite list once the image is loaded.
    /*
    LaunchedEffect(astroBoyImage) {

        if (astroBoyImage != null) {
            // Create first sprite moving right
            //val sprite1 = Sprite(image = astroBoyImage, position = mutableStateOf(Offset(100f, 100f)))
            //sprite1.setVelocity(0f, 0f) // Move right

            // Create second sprite moving left
            val sprite2 = Player(image = astroBoyImage,id = mutableIntStateOf(count++), position = mutableStateOf(Offset(600f, 2000f)))
            //sprite2.setVelocity(-100f, 0f) // Move left
            // Add both sprites
            sprites.addAll(listOf(sprite2))
        }
    }
    */

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

    // Continuously Spawn Falling Objects Randomly
    LaunchedEffect(fishImage) {
        while (true) {
            if (fishImage != null) {
                val spawnX = Random.nextFloat() * (screenWidthPx - 100f)
                val skyItem = SkyItems(
                    image = fishImage,
                    id = mutableIntStateOf(count++),
                    position = mutableStateOf(Offset(spawnX, -100f)) // Start offscreen
                )
                skyItem.setVelocity(0f, Random.nextFloat() * 10f + 5f) // Random falling speed
                sprites.add(skyItem)
                skyItems.add(skyItem)
            }
            delay(1500L) // Spawn every 1.5 seconds
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
        val frameTimeMs = 16L       // ~60fps
        val dt = frameTimeMs / 1000f
        val sensitivity = 800f      //testing
        val friction = 0.9f       // damping for testing
        val gravity = 5f

        while (true) {
            // Update sensor-controlled sprite (sprite1) using accelerometer data.
            if (sprites.isNotEmpty()) {
                val player = sprites[0]
                // Use raw sensor input as the indicator for zeroing velocity.
                if (abs(latestAxRaw) < 0.01f) {
                    player.velocity.value = Offset.Zero
                } else {
                    val accelerationX = -latestAx * sensitivity
                    val newVelX = player.velocity.value.x + accelerationX * dt
                    val updatedVelX = newVelX * friction
                    player.velocity.value = if (abs(updatedVelX) < 0.1f)
                        Offset.Zero
                    else
                        Offset(updatedVelX, player.velocity.value.y)
                }
            }


            // Update all sprites & apply gravity to falling objects
            sprites.forEach { sprite ->
                sprite.update(dt, screenWidthPx, screenHeightPx)

                if (sprite !is Player) { // Apply gravity to falling objects
                    sprite.velocity.value = Offset(sprite.velocity.value.x, sprite.velocity.value.y + gravity * dt)
                }

                // Remove objects that reach the bottom
                if (sprite.position.value.y >= screenHeightPx) {
                    sprite.isAlive.value = false // Only remove if it hits the bottom
                }
            }

            // Remove sprites that are not alive **after iteration**
            sprites.removeAll { !it.isAlive.value }

            // Do things if got collision
            //var collisionDetected = false
            for (i in sprites.indices) {
                for (j in i + 1 until sprites.size) {
                    if (sprites[i].checkCollision(sprites[j])) {
                        sprites[i].color.value = Color.Red
                        sprites[j].color.value = Color.Red
                        //collisionDetected = true
                        println("Collision detected between Sprite $i and Sprite $j")
                    }
                }
            }

            // If no collision detected, reset colors
            sprites.forEach { it.color.value = Color.White }

            /*
            // Do things if no collision
            if (!collisionDetected) {
                sprites.forEach { it.color.value = Color.White }
                //println("Collision not detected")
            }
             */

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
            Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                Text("axRaw = $axRaw, axFiltered = $axFiltered")
                if (sprites.isNotEmpty()) {
                    Text("Sprite1 velocityX = ${sprites[0].velocity.value.x}")
                    Text("Sprite1 X = ${sprites[0].position.value.x}")
                }
                //Text("ayRaw = $ayRaw, azRaw = $azRaw")
            }
        }
    }
}