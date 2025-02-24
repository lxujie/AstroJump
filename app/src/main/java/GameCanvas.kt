// File: GameCanvas.kt
package astrojump

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
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.Color
import astrojump.input.accelerometerSensor
import astrojump.input.rememberFilteredAcceleration
import androidx.compose.ui.layout.ContentScale
import kotlin.math.abs
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import astrojump.data.GameDatabase
import astrojump.data.GameSession
import astrojump.data.HighScore
import astrojump.model.ObjectType
import astrojump.model.Player
import astrojump.model.SkyItems
import astrojump.util.SFXManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

var count = 0

@Composable
fun GameScreen(navController: NavHostController) {
    GameCanvas(navController)
}

@Composable
fun GameCanvas(navController: NavHostController) {

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() } // Convert to pixels
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() } // Get screen width

    // Load assets
    val backgroundImage = loadImageFromAssets("Space.png")
    val astroBoyImage = loadImageFromAssets("AstroBoy1.png")
    val asteroidImage = loadImageFromAssets("Asteroid.png")
    val starImage = loadImageFromAssets("Star.png")

    // Game sprites and sky item lists
    val sprites = remember { mutableStateListOf<Sprite>() }
    val skyItems = remember { mutableStateListOf<SkyItems>() }

    // Game State Variables
    var playerHealth by remember { mutableIntStateOf(5) }  // Player starts with 5 health
    var playerScore by remember { mutableIntStateOf(0) }   // Start score at 0
    var gameOver by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val db = GameDatabase.getDatabase(context)
    LaunchedEffect(Unit) {
        SFXManager.init(context)
    }
    var hasNavigated by remember { mutableStateOf(false) }

    // Create Player Sprite when asset is loaded
    LaunchedEffect(astroBoyImage) {
        if (astroBoyImage != null) {
            val player = Player(
                image = astroBoyImage,
                id = mutableIntStateOf(count++),
                position = mutableStateOf(Offset(screenWidthPx / 2f, screenHeightPx - 300f)),
                scale = mutableFloatStateOf(0.5f)
            )
            sprites.add(player) // Add player sprite
        }
    }

    // Spawn sky items periodically (both good and bad objects)
    LaunchedEffect(asteroidImage, starImage) {
        while (true) {
            // Read the current score from state
            val currentScore = playerScore

            // Determine the speed multiplier based on the player's score.
            // Adjust thresholds and multipliers as needed.
            val multiplier = when {
                currentScore >= 1000 -> 1.2f
                currentScore >= 500  -> 1.1f
                else                -> 1f
            }

            // Determine delay range based on player's score.
            val (minDelay, maxDelay) = when {
                currentScore >= 10000 -> Pair(1000L, 1500L)
                currentScore >= 5000 -> Pair(2000L, 2500L)
                currentScore >= 1000 -> Pair(3000L, 3500L)
                currentScore >= 500  -> Pair(4000L, 4500L)
                else                -> Pair(5000L, 5500L)
            }

            // Define a threshold (in pixels) to ensure a minimum horizontal separation.
            val separationThreshold = 50f // Adjust as needed based on object sizes

            // Generate random positions for the objects.
            val randomX = Random.nextFloat() * screenWidthPx
            var randomX2 = Random.nextFloat() * screenWidthPx

            // If both images are available, ensure they are not too close.
            if (asteroidImage != null && starImage != null) {
                while (abs(randomX - randomX2) < separationThreshold) {
                    randomX2 = Random.nextFloat() * screenWidthPx
                }
            }

            val rng = when{
                currentScore >= 8000 -> 5
                currentScore >= 4000 -> 4
                currentScore >= 2000 -> 3
                currentScore >= 250  -> 2
                else                -> 1
            }

            if ((Random.nextInt(0,5)) < rng ) {
                // Calculate the bad object's falling speed.
                val baseBadVelocity = Random.nextFloat() * 1f + 1f
                val badVelocity = baseBadVelocity * multiplier

                asteroidImage?.let {
                    val badObject = SkyItems(
                        image = it,
                        id = mutableIntStateOf(count++),
                        position = mutableStateOf(Offset(randomX, 0f)),
                        scale = mutableFloatStateOf(2f),
                        //rotation = mutableFloatStateOf(270f),
                        type = ObjectType.BAD
                    )
                    badObject.setVelocity(0f, badVelocity)
                    sprites.add(badObject)
                    skyItems.add(badObject)
                }
            }
            // Calculate the good object's falling speed.
            val baseGoodVelocity = Random.nextFloat() * 1f + 0.5f
            val goodVelocity = baseGoodVelocity * multiplier

            starImage?.let {
                val goodObject = SkyItems(
                    image = it,
                    id = mutableIntStateOf(count++),
                    position = mutableStateOf(Offset(randomX2, 0f)),
                    //rotation = mutableFloatStateOf(355f),
                    scale = mutableFloatStateOf(0.5f),
                    type = ObjectType.GOOD
                )
                goodObject.setVelocity(0f, goodVelocity)
                sprites.add(goodObject)
                skyItems.add(goodObject)
            }

            delay(Random.nextLong(minDelay, maxDelay))
        }
    }

    // Read accelerometer sensor values
    val (axRaw, ayRaw, azRaw) = accelerometerSensor()
    val (axFiltered, _, _) = rememberFilteredAcceleration(
        rawX = axRaw,
        rawY = ayRaw,
        rawZ = azRaw,
        alpha = 0.5f, // moderate smoothing
        threshold = 0f //no dead zone for debugging
    )

    // Always get the latest axFiltered and axRaw value
    val latestAx by rememberUpdatedState(newValue =  axFiltered)
    val latestAxRaw by rememberUpdatedState(newValue = axRaw)

    // Game loop that updates sprites and integrates sensor input
    LaunchedEffect(Unit) {
        val frameTimeMs = 16L
        val dt = frameTimeMs / 1000f
        val sensitivity = 200f
        val friction = 0.9f

        while (true) {
            val player = sprites.firstOrNull { it is Player } as? Player

            player?.let {
                // Calculate tilt magnitude and use it to dynamically adjust sensitivity
                val tiltMagnitude = abs(latestAxRaw)
                if (tiltMagnitude < 0.01f) {
                    it.velocity.value = Offset.Zero
                } else {
                    val tiltMultiplier = 1 + tiltMagnitude // increase multiplier based on tilt
                    val dynamicSensitivity = sensitivity * tiltMultiplier
                    val accelerationX = -latestAx * dynamicSensitivity
                    val newVelX = it.velocity.value.x + accelerationX * dt
                    it.velocity.value = Offset(newVelX * friction, it.velocity.value.y)
                }
            }

            val collidedObjects = mutableListOf<Sprite>()
            val outOfBoundsObjects = mutableListOf<Sprite>()

            // Update all sprites, handle collisions and remove out-of bound objects
            sprites.forEach { sprite ->
                sprite.update(dt, screenWidthPx, screenHeightPx)
                if (sprite.position.value.y >= screenHeightPx) {
                    // If a GOOD object reaches the bottom, reduce player health
                    if (sprite is SkyItems && sprite.type == ObjectType.GOOD) {
                        playerHealth = (playerHealth - 1).coerceAtLeast(0)
                    }
                    outOfBoundsObjects.add(sprite)
                }
                if (player != null && sprite is SkyItems && player.checkCollision(sprite)) {
                    SFXManager.playCollide()

                    when (sprite.type) {
                        ObjectType.BAD -> {
                            playerHealth = (playerHealth - 1).coerceAtLeast(0)
                            if (playerHealth == 0 && !gameOver) {
                                SFXManager.playDeath()
                                gameOver = true // Set immediately so UI updates, but delay navigation

                                // Update the database
                                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                                    val highScoreDao = db.highScoreDao()
                                    val gameSessionDao = db.gameSessionDao()
                                    val savedHighScore = highScoreDao.getHighScore()?.score ?: 0

                                    if (playerScore > savedHighScore) {
                                        highScoreDao.insertHighScore(HighScore(id = 0, score = playerScore))
                                    }
                                    gameSessionDao.insertGameSession(
                                        GameSession(score = playerScore, date = System.currentTimeMillis())
                                    )

                                    withContext(Dispatchers.Main) {
                                        delay(1000) // Delay to prevent recomposition issues
                                        if (!hasNavigated) {
                                            hasNavigated = true
                                            navController.navigate("gameOver")
                                        }
                                    }
                                }
                            }
                        }
                        ObjectType.GOOD -> {
                            if (!gameOver) {
                                playerScore += 100
                            }
                        }
                    }
                    collidedObjects.add(sprite)
                }
            }

            sprites.removeAll(collidedObjects + outOfBoundsObjects)
            skyItems.removeAll((collidedObjects + outOfBoundsObjects).toSet())

            delay(frameTimeMs)
        }
    }

    var highScore by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()


    // Update high score when the game is over (or when the composable first loads)
    LaunchedEffect(gameOver) {
        // When the game is over, or initially, read the high score from the DB
        coroutineScope.launch {
            highScore = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                db.highScoreDao().getHighScore()?.score ?: 0
            }
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

                    withTransform({
                        // Move to sprite center
                        translate(sprite.position.value.x + sprite.width / 2, sprite.position.value.y + sprite.height / 2)
                        scale(scale = sprite.scale.value, pivot = Offset.Zero)
                        rotate(degrees = sprite.rotation.value, pivot = Offset.Zero)
                        translate(-sprite.width / (2 * sprite.scale.value), -sprite.height / (2 * sprite.scale.value))
                    }) {
                        drawImage(image = sprite.image)
                    }

                    // Draw the updated bounding box
                    /*
                    drawRect(
                        color = Color.Green,
                        topLeft = Offset(sprite.boundingBox.left, sprite.boundingBox.top),
                        size = androidx.compose.ui.geometry.Size(
                            width = sprite.boundingBox.width,
                            height = sprite.boundingBox.height
                        ),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                    )
                    */
                }
            }

            // Display Health & Score
            Column(modifier = Modifier.align(Alignment.TopCenter)) {
                Text(text = "Health: $playerHealth", color = Color.Red)
                Text(text = "Score: $playerScore", color = Color.Yellow)
                Text(text = "High Score: $highScore", color = Color.Green)
            }

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

