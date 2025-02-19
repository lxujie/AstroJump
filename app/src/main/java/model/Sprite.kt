// File: Sprite.kt
package astrojump.model

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap

data class Sprite(
    val image: ImageBitmap,
    var position: MutableState<Offset> = mutableStateOf(Offset.Zero),
    var rotation: MutableState<Float> = mutableStateOf(0f),
    var scale: MutableState<Float> = mutableStateOf(1f)
) {
    fun update(deltaTime: Long) {
        position.value = position.value.copy(x = position.value.x + 1f)
    }
}
