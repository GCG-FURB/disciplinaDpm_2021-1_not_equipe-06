package com.furb.br.nathan.dispositivosmoveis3d

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.util.Log
import com.furb.br.nathan.dispositivosmoveis3d.tools.isPositive
import kotlin.math.abs

class AccelerometerSensorEventListener(private val acceleration: Acceleration) : SensorEventListener {

    private var accuracy = 0

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(this.javaClass.simpleName, "Accuracy: $accuracy")
        this.accuracy = accuracy
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        Log.d(this.javaClass.simpleName, "x$x, y$y, z$z")

        handleX(x)
        handleY(y)

        Log.d(this.javaClass.simpleName, "Acceleration: $acceleration")
    }

    private fun handleX(x: Float) {
        if (!shouldUpdateAccelerationCoordinate(x)) return

        if (x.isPositive()) {
            acceleration.accelerateLeft(1)
        } else {
            acceleration.accelerateRight(1)
        }
    }

    private fun handleY(y: Float) {
        if (!shouldUpdateAccelerationCoordinate(y)) return

        if (y.isPositive()) {
            acceleration.accelerateDown(1)
        } else {
            acceleration.accelerateUp(1)
        }
    }

    private fun shouldUpdateAccelerationCoordinate(coordinate: Float) = abs(coordinate) > accuracy
}