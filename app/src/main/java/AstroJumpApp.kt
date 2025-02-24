// File: AstroJumpApp.kt
package astrojump

import android.app.Activity
import android.app.Application
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import edu.singaporetech.astrojump.R

class AstroJumpApp : Application() {

    lateinit var mediaPlayer: MediaPlayer
    private var activityReferences = 0
    private var isActivityChangingConfigurations = false

    override fun onCreate() {
        super.onCreate()

        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.bgm)
            mediaPlayer.isLooping = true
            mediaPlayer.start()
        } catch (e: Exception) {
            Log.e("AstroJumpApp", "Error initializing MediaPlayer", e)
        }

        // Register lifecycle callbacks to manage BGM across activities.
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) { }

            override fun onActivityStarted(activity: Activity) {
                activityReferences++
                // If transitioning from 0 to 1 activity, the app is entering the foreground.
                if (activityReferences == 1 && !isActivityChangingConfigurations) {
                    resumeBGM()
                }
            }

            override fun onActivityResumed(activity: Activity) { }

            override fun onActivityPaused(activity: Activity) { }

            override fun onActivityStopped(activity: Activity) {
                activityReferences--
                isActivityChangingConfigurations = activity.isChangingConfigurations
                // When no activity is visible, pause the BGM.
                if (activityReferences == 0 && !isActivityChangingConfigurations) {
                    pauseBGM()
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) { }

            override fun onActivityDestroyed(activity: Activity) { }
        })
    }

    fun pauseBGM() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            Log.d("AstroJumpApp", "BGM paused")
        }
    }

    fun resumeBGM() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            Log.d("AstroJumpApp", "BGM resumed")
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }
}
