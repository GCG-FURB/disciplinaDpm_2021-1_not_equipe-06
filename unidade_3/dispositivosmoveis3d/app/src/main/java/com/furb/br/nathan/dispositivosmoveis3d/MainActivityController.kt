package com.furb.br.nathan.dispositivosmoveis3d

import androidx.constraintlayout.widget.ConstraintLayout

class MainActivityController {

    private val acceleration = Acceleration()
    val sensorEventListener = AccelerometerSensorEventListener(acceleration)

    fun moveFunction(params: ConstraintLayout.LayoutParams): ConstraintLayout.LayoutParams {
        if (acceleration.isStationary()) return params

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

        return params
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
}