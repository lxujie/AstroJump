package astrojump.input

import androidx.compose.runtime.*
import kotlin.math.sqrt

/**
 * Encapsulates optional input logic using the raw accelerometer data.
 * This can be toggled on/off based on our game’s needs.
 */

@Composable
fun rememberFilteredAcceleration(
    rawX: Float,
    rawY: Float,
    rawZ: Float,
    alpha: Float = 0.8f,
    threshold: Float = 0.05f
): Triple<Float, Float, Float> {
    // Keep track of old filtered values
    var filteredX by remember { mutableFloatStateOf(rawX) }
    var filteredY by remember { mutableFloatStateOf(rawY) }
    var filteredZ by remember { mutableFloatStateOf(rawZ) }

    // We recalculate filtered values whenever raw changes
    LaunchedEffect(rawX, rawY, rawZ) {
        filteredX = alpha * filteredX + (1 - alpha) * rawX
        filteredY = alpha * filteredY + (1 - alpha) * rawY
        filteredZ = alpha * filteredZ + (1 - alpha) * rawZ

        // Optional “dead zone” threshold to ignore small movements
        if (kotlin.math.abs(filteredX) < threshold) filteredX = 0f
        if (kotlin.math.abs(filteredY) < threshold) filteredY = 0f
        if (kotlin.math.abs(filteredZ) < threshold) filteredZ = 0f
    }

    return Triple(filteredX, filteredY, filteredZ)
}

/**
 * Utility to convert raw or filtered accelerometer values into horizontal velocity.
 * - sensitivity: how strongly tilt translates to movement
 * - friction: how quickly velocity dampens
 */
@Composable
fun rememberHorizontalMovement(
    accelX: Float,
    sensitivity: Float = 1.5f,
    friction: Float = 0.95f
): Float {
    var velocityX by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(accelX) {
        // Add some velocity based on sensor reading
        velocityX += accelX * sensitivity
        // Apply friction
        velocityX *= friction
    }

    return velocityX
}

/**
 * Example “shake” detection or strong movement detection.
 * If you want to detect events like a ‘jump’ or a special move, call this.
 */
@Composable
fun DetectShakeEvent(
    accelX: Float,
    accelY: Float,
    accelZ: Float,
    shakeThreshold: Float = 12f,
    onShake: () -> Unit = {}
) {
    LaunchedEffect(accelX, accelY, accelZ) {
        val magnitude = sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ)
        if (magnitude > shakeThreshold) {
            onShake()
        }
    }
}
