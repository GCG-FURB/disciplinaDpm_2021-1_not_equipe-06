package com.furb.br.nathan.dispositivosmoveis3d

class Acceleration(var up: Int = 0, var down: Int = 0, var left: Int = 0, var right: Int = 0) {

    fun isStationary() = !(isAcceleratingUp() or isAcceleratingRight() or isAcceleratingDown() or isAcceleratingLeft())

    fun isAcceleratingUp() = up > 0
    fun isAcceleratingUpRight() = isAcceleratingUp() and isAcceleratingRight()
    fun isAcceleratingRight() = right > 0
    fun isAcceleratingDownRight() = isAcceleratingDown() and isAcceleratingRight()
    fun isAcceleratingDown() = down > 0
    fun isAcceleratingDownLeft() = isAcceleratingDown() and isAcceleratingLeft()
    fun isAcceleratingLeft() = left > 0
    fun isAcceleratingUpLeft() = isAcceleratingUp() and isAcceleratingLeft()

    fun accelerateUp(acceleration: Int) {
        if (down == 0)
            up += acceleration
        if (down > 0)
            down -= acceleration
    }

    fun accelerateRight(acceleration: Int) {
        if (left == 0)
            right += acceleration
        if (left > 0)
            left -= acceleration
    }

    fun accelerateDown(acceleration: Int) {
        if (up == 0)
            down += acceleration
        if (up > 0)
            up -= acceleration
    }

    fun accelerateLeft(acceleration: Int) {
        if (right == 0)
            left += acceleration
        if (right > 0)
            right -= acceleration
    }

    override fun toString(): String {
        return "up: $up, right: $right, down: $down, left: $left"
    }
}