package com.furb.br.nathan.dispositivosmoveis3d

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.math.abs

class MainActivity : AppCompatActivity(), SensorEventListener {

    private var currentSensor: Sensor? = null
    private var accuracy = 1

    private val acceleration = Acceleration()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupAccelerometer()

        val movableView = this.findViewById<View>(R.id.moving_view)
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run() {
                moveFunction(movableView)
                mainHandler.postDelayed(this, 200)
            }
        })
    }

    private fun setupAccelerometer() {
        setupSensor(Sensor.TYPE_ACCELEROMETER)
    }

    private fun setupGyroscope() {
        setupSensor(Sensor.TYPE_GYROSCOPE)
    }

    private fun setupSensor(sensorType: Int) {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        if (currentSensor != null) {
            sensorManager.unregisterListener(this, currentSensor)
        }

        val sensor = sensorManager.getDefaultSensor(sensorType)
        if (sensor == null) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Phone does not have an accelerometer, this function won't work")
        }

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)
        currentSensor = sensor
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(this.localClassName, "Accuracy: $accuracy")
        this.accuracy = accuracy
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        Log.d(this.localClassName, "x$x, y$y, z$z")

        handleX(x)
        handleY(y)

        Log.d(this.localClassName, "Acceleration: $acceleration")
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

    private fun moveFunction(view: View) {
        if (acceleration.isStationary()) return

        val params = view.layoutParams as ConstraintLayout.LayoutParams
        if (acceleration.isAcceleratingUp()) {
            handleUpAcceleration(params)
        }
        if (acceleration.isAcceleratingRight()) {
            handleRightAcceleration(params)
        }
        if (acceleration.isAcceleratingDown()) {
            handleBottomAcceleration(params)
        }
        if (acceleration.isAcceleratingLeft()) {
            handleLeftAcceleration(params)
        }

        view.requestLayout()
    }

    private fun handleUpAcceleration(params: ConstraintLayout.LayoutParams) {
        val topMargin = params.topMargin
        val accelerationUp = acceleration.up

        if (validateAcceleration(topMargin, accelerationUp)) {
            params.bottomMargin += topMargin
            params.topMargin = 0
            acceleration.up = 0

            return
        }

        params.topMargin -= accelerationUp
        params.bottomMargin += accelerationUp
    }

    private fun handleRightAcceleration(params: ConstraintLayout.LayoutParams) {
        val rightMargin = params.rightMargin
        val accelerationRight = acceleration.right

        if (validateAcceleration(rightMargin, accelerationRight)) {
            params.leftMargin += rightMargin
            params.rightMargin = 0
            acceleration.right = 0

            return
        }

        params.rightMargin -= accelerationRight
        params.leftMargin += accelerationRight
    }

    private fun handleBottomAcceleration(params: ConstraintLayout.LayoutParams) {
        val bottomMargin = params.bottomMargin
        val accelerationDown = acceleration.down

        if (validateAcceleration(bottomMargin, accelerationDown)) {
            params.topMargin += bottomMargin
            params.bottomMargin = 0
            acceleration.down = 0

            return
        }

        params.bottomMargin -= accelerationDown
        params.topMargin += accelerationDown
    }

    private fun handleLeftAcceleration(params: ConstraintLayout.LayoutParams) {
        val leftMargin = params.leftMargin
        val accelerationLeft = acceleration.left

        if (validateAcceleration(leftMargin, accelerationLeft)) {
            params.rightMargin += leftMargin
            params.leftMargin = 0
            acceleration.left = 0

            return
        }

        params.leftMargin -= accelerationLeft
        params.rightMargin += accelerationLeft
    }

    private fun validateAcceleration(distance: Int, acceleration: Int) = distance < acceleration

    private fun shouldUpdateAccelerationCoordinate(coordinate: Float) = abs(coordinate) > accuracy

    private fun Float.isPositive() = this > 0
}