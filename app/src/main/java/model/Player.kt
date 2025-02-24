// File: Player.kt
package model

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Color

class Player(
    image: ImageBitmap,
    id: MutableState<Int> = mutableIntStateOf(0),
    position: MutableState<Offset> = mutableStateOf(Offset.Zero),
    rotation: MutableState<Float> = mutableFloatStateOf(0f),
    scale: MutableState<Float> = mutableFloatStateOf(1f),
    color: MutableState<Color> = mutableStateOf(Color.White), // Default color
    velocity: MutableState<Offset> = mutableStateOf(Offset.Zero), // Movement speed
    isAlive: MutableState<Boolean> = mutableStateOf(true)
) : Sprite(image, id, position, rotation, scale, color, velocity) {

    override fun update(dt: Float, screenWidth: Float, screenHeight: Float) {
        val gravity = 9.8f * 50 // Adjust gravity strength for the game

        var newX = position.value.x + velocity.value.x * dt
        val newY = position.value.y + velocity.value.y + gravity * dt

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
}
