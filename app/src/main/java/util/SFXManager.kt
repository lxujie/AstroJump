// File: SFXManager.kt
package astrojump.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import edu.singaporetech.astrojump.R

object SFXManager {
    private lateinit var soundPool: SoundPool
    private var collideSoundId: Int = 0
    private var deathSoundId: Int = 0
    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        collideSoundId = soundPool.load(context, R.raw.collide, 1)
        deathSoundId = soundPool.load(context, R.raw.death, 1)
        isInitialized = true
    }

    fun playCollide() {
        if (isInitialized) {
            soundPool.play(collideSoundId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun playDeath() {
        if (isInitialized) {
            soundPool.play(deathSoundId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun release() {
        if (isInitialized) {
            soundPool.release()
            isInitialized = false
        }
    }
}
