// GameSession.kt
package astrojump.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_session_table")
data class GameSession(
    @PrimaryKey(autoGenerate = true) val sessionId: Int = 0,
    val score: Int,
    val date: Long // Timestamp in milliseconds.
)
