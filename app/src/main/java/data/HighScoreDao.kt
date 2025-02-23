// HighScoreDao.kt
package astrojump.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HighScoreDao {
    @Query("SELECT * FROM high_score_table WHERE id = 0")
    suspend fun getHighScore(): HighScore?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHighScore(highScore: HighScore)
}
