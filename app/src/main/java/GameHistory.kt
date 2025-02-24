import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import astrojump.data.GameDatabase
import astrojump.data.GameSession
import util.loadImageFromAssets
import astrojump.ui.theme.rememberCustomFont
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GameHistory(navController: NavHostController) {
    // Load assets to match the main menu style.
    val backgroundImage = loadImageFromAssets("Space.png")?.let { BitmapPainter(it) }
    val buttonImage = loadImageFromAssets("Button.png")?.let { BitmapPainter(it) }
    val customFont = rememberCustomFont()

    // Access the database and load game session history.
    val context = androidx.compose.ui.platform.LocalContext.current
    val db = GameDatabase.getDatabase(context)
    var sessions by remember { mutableStateOf<List<GameSession>>(emptyList()) }

    LaunchedEffect(Unit) {
        sessions = withContext(Dispatchers.IO) {
            db.gameSessionDao().getAllSessions()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        backgroundImage?.let {
            Image(
                painter = it,
                contentDescription = "Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        // Main content column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title aligned with Main Menu styling.
            Text(
                text = "Game ",
                fontFamily = customFont,
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "History ",
                fontFamily = customFont,
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Display the list of game sessions.
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sessions) { session ->
                    // Format the date.
                    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                    val formattedDate = dateFormat.format(Date(session.date))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Score: ${session.score}",
                            color = Color.White,
                            fontSize = 18.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = formattedDate,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            buttonImage?.let {
                ButtonWithImage(
                    buttonImage = it,
                    text = "Back",
                    onClick = { navController.popBackStack() }
                )
            }
        }
    }
}
