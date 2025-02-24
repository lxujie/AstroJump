package util

import android.graphics.BitmapFactory
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun loadImageFromAssets(assetPath: String): ImageBitmap? {
    val context = LocalContext.current
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(assetPath) {
        withContext(Dispatchers.IO) {
            try {
                context.assets.open(assetPath).use { inputStream ->
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    imageBitmap = bitmap?.asImageBitmap()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    return imageBitmap
}

@Composable
fun loadJsonFromAssets(assetPath: String): String? {
    val context = LocalContext.current
    var jsonString by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(assetPath) {
        withContext(Dispatchers.IO) {
            try {
                context.assets.open(assetPath).use { inputStream ->
                    jsonString = inputStream.bufferedReader().use { it.readText() }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    return jsonString
}