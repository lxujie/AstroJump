package astrojump.util

import android.view.SoundEffectConstants
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import astrojump.ui.theme.rememberCustomFont

@Composable
fun ButtonWithImage(buttonImage: BitmapPainter, text: String, onClick: () -> Unit) {
    val customFont = rememberCustomFont()
    val view = LocalView.current

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                view.playSoundEffect(SoundEffectConstants.CLICK) // Play default Android click sound
                onClick()
            }
    ) {
        Image(
            painter = buttonImage,
            contentDescription = "Button Background",
            modifier = Modifier.size(200.dp, 80.dp),
            contentScale = ContentScale.FillWidth
        )

        Text(
            text = text,
            fontFamily = customFont,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}