package astrojump.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import edu.singaporetech.astrojump.ui.theme.Pink40
import edu.singaporetech.astrojump.ui.theme.Pink80
import edu.singaporetech.astrojump.ui.theme.Purple40
import edu.singaporetech.astrojump.ui.theme.Purple80
import edu.singaporetech.astrojump.ui.theme.PurpleGrey40
import edu.singaporetech.astrojump.ui.theme.PurpleGrey80

import android.content.Context
import android.graphics.Typeface
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.*
import edu.singaporetech.astrojump.R

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun rememberCustomFont(): FontFamily {
    val context = LocalContext.current
    return remember {
        FontFamily(
            Font(resId = R.font.vtks)
        )
    }
}