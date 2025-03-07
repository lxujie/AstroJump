// File: Sprite.kt
package astrojump.model

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

open class Sprite(
    val image: ImageBitmap,
    var id: MutableState<Int> = mutableIntStateOf(0),
    var position: MutableState<Offset> = mutableStateOf(Offset.Zero),
    var rotation: MutableState<Float> = mutableFloatStateOf(0f),
    var scale: MutableState<Float> = mutableFloatStateOf(1f),
    var color: MutableState<Color> = mutableStateOf(Color.White), // Default color
    var velocity: MutableState<Offset> = mutableStateOf(Offset.Zero), // Movement speed
    var isAlive: MutableState<Boolean> = mutableStateOf(true)
) {

    // Correct width and height using scale
    val width: Float get() = image.width.toFloat() * scale.value
    val height: Float get() = image.height.toFloat() * scale.value


    val boundingBox: Rect
        get() {
            val scaledWidth = image.width * scale.value
            val scaledHeight = image.height * scale.value
            val halfWidth = scaledWidth / 2
            val halfHeight = scaledHeight / 2

            val centerX = position.value.x + halfWidth
            val centerY = position.value.y + halfHeight

            // Convert degrees to radians
            val rad = (rotation.value * PI / 180.0).toFloat()
            val cosTheta = cos(rad)
            val sinTheta = sin(rad)

            // Compute the correct bounding box with scaling and positioning
            return Rect(
                left = centerX - (halfWidth * abs(cosTheta) + halfHeight * abs(sinTheta)),
                top = centerY - (halfWidth * abs(sinTheta) + halfHeight * abs(cosTheta)),
                right = centerX + (halfWidth * abs(cosTheta) + halfHeight * abs(sinTheta)),
                bottom = centerY + (halfWidth * abs(sinTheta) + halfHeight * abs(cosTheta))
            )
        }


    open fun update(dt: Float, screenWidth: Float, screenHeight: Float) {
        var newX = position.value.x + velocity.value.x * dt
        val newY = position.value.y + velocity.value.y * dt

        // Prevent the player from moving off the left or right edge of the screen
        newX = newX.coerceIn(0f, screenWidth - width)

        position.value = Offset(newX, newY)

        // Prevent the player from falling through the floor
        if (boundingBox.bottom >= screenHeight) {
            position.value = position.value.copy(y = screenHeight - height)
            velocity.value = Offset(velocity.value.x, 0f) // Stop downward movement
            //println("DEBUG: Player collided with the floor at Y = ${position.value.y}")
        }
    }


    // Function to set movement direction
    fun setVelocity(x: Float, y: Float) {
        velocity.value = Offset(x, y)
    }

    fun checkCollision(other: Sprite): Boolean {
        return this.boundingBox.overlaps(other.boundingBox)
    }

    // Function to check collision with the floor
    fun checkFloorCollision(screenHeight: Float): Boolean {
        return boundingBox.bottom >= screenHeight
    }
}
