// HighScore.kt
package astrojump.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "high_score_table")
data class HighScore(
    @PrimaryKey val id: Int = 0, // We'll always use row 0 for the single high score.
    val score: Int
)
