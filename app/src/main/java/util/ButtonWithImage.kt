// File: ButtonWithImage.kt
package astrojump.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import astrojump.ui.theme.rememberCustomFont
import androidx.compose.ui.graphics.Color

@Composable
fun ButtonWithImage(buttonImage: BitmapPainter, text: String, onClick: () -> Unit) {
    val customFont = rememberCustomFont()
    Box(
        modifier = Modifier
            .size(width = 250.dp, height = 80.dp)
            .clickable { onClick() }
    ) {
        Image(
            painter = buttonImage,
            contentDescription = "Button Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = customFont
            )
        }
    }
}
