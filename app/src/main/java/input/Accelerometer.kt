package astrojump.input

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.DisposableEffect

/*
class Accelerometer {
}*/

@Composable
fun accelerometerSensor(): Triple<Float, Float, Float> {
    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(SensorManager::class.java)
    }

    var accelValues by remember { mutableStateOf(Triple(0f, 0f, 0f)) }

    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                    accelValues = Triple(event.values[0], event.values[1], event.values[2])
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        // Register the sensor
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager?.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)

        // Unregister when this composable is disposed (e.g., if user navigates away)
        onDispose {
            sensorManager?.unregisterListener(listener)
        }
    }
    return accelValues
}