package com.xyf.common.video

import com.xyf.common.annotation.UiThread
import java.util.*

class ProcessHelper {
    @UiThread
    fun tick(progress: Double): Long {
        val speed = (progress - lastProgress) / (System.currentTimeMillis() - lastTime)
        lastProgress = progress
        lastTime = System.currentTimeMillis()
        return ((1 - progress) / calculateSpeed(speed)).toLong()
    }

    @UiThread
    private fun calculateSpeed(speed: Double): Double {
        speeds.push(speed)
        if (speeds.size > 10) {
            speeds.pop()
        }
        var total = 0.0
        for (s in speeds) {
            total += s
        }
        return total / speeds.size
    }

    private val speeds = LinkedList<Double>()
    private var lastTime = System.currentTimeMillis()
    private var lastProgress = 0.0
}