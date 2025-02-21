// File: Sprite.kt
package astrojump.model

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color

data class Sprite(
    val image: ImageBitmap,
    var position: MutableState<Offset> = mutableStateOf(Offset.Zero),
    var rotation: MutableState<Float> = mutableFloatStateOf(0f),
    var scale: MutableState<Float> = mutableFloatStateOf(1f),
    var color: MutableState<Color> = mutableStateOf(Color.White), // Default color
    var velocity: MutableState<Offset> = mutableStateOf(Offset.Zero) // Movement speed
) {

    // Correct width and height using scale
    val width: Float get() = image.width.toFloat() * scale.value
    val height: Float get() = image.height.toFloat() * scale.value

    val boundingBox: Rect
        get() = Rect(
            left = position.value.x, // Adjust if needed
            top = position.value.y,
            right = position.value.x + width,
            bottom = position.value.y + height
        )

    // Update function now moves sprite based on velocity
    fun update(dt: Float) {
        position.value = position.value.copy(
            x = position.value.x + velocity.value.x * dt,
            y = position.value.y + velocity.value.y * dt
        )
    }

    // Function to set movement direction
    fun setVelocity(x: Float, y: Float) {
        velocity.value = Offset(x, y)
    }

    fun checkCollision(other: Sprite): Boolean {
        return this.boundingBox.overlaps(other.boundingBox)
    }
}
