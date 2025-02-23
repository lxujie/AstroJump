// GameSessionDao.kt
package astrojump.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameSessionDao {
    @Query("SELECT * FROM game_session_table ORDER BY date DESC")
    suspend fun getAllSessions(): List<GameSession>

    @Insert
    suspend fun insertGameSession(gameSession: GameSession)
}
