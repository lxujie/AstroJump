// File: Sprite.kt
package astrojump.model

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap

data class Sprite(
    val image: ImageBitmap,
    var position: MutableState<Offset> = mutableStateOf(Offset.Zero),
    var velocity: MutableState<Offset> = mutableStateOf(Offset.Zero),
    var rotation: MutableState<Float> = mutableFloatStateOf(0f),
    var scale: MutableState<Float> = mutableFloatStateOf(1f)
) {
    fun update(deltaTime: Long) {
        val dtSeconds = deltaTime / 1000f
        position.value = Offset(
            position.value.x + velocity.value.x * dtSeconds,
            position.value.y + velocity.value.y * dtSeconds
        )
    }
}
