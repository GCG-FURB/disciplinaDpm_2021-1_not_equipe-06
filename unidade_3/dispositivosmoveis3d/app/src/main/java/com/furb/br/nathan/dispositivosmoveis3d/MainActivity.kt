package com.furb.br.nathan.dispositivosmoveis3d

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {

    val controller: MainActivityController = MainActivityController()

    lateinit var mainHandler: Handler
    lateinit var movableView: View
    private val moveViewTask = object : Runnable {
        override fun run() {
            val params = movableView.layoutParams as ConstraintLayout.LayoutParams
            controller.moveFunction(params)
            movableView.requestLayout()

            mainHandler.postDelayed(this, 200)
        }
    }

    private lateinit var sensorManager: SensorManager
    private var currentSensor: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        setupAccelerometerSensor()

        movableView = this.findViewById<View>(R.id.moving_view)
        mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(moveViewTask)
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(moveViewTask)
        removeSensorListener()
    }

    override fun onResume() {
        super.onResume()
        mainHandler.post(moveViewTask)
        setupAccelerometerSensor()
    }

    private fun setupAccelerometerSensor() {
        removeSensorListener()

        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (sensor == null) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Phone does not have an accelerometer, this function won't work")
        }

        sensorManager.registerListener(controller.sensorEventListener, sensor, SensorManager.SENSOR_DELAY_GAME)
        currentSensor = sensor
    }

    private fun removeSensorListener() {
        if (currentSensor != null) {
            sensorManager.unregisterListener(controller.sensorEventListener, currentSensor)
        }
    }
}