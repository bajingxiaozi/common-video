package com.xyf.common.video

import com.xyf.common.annotation.UiThread
import java.io.File
import java.util.*

class ConvertProgressHelper(file: File) {

    private val totalFrame = VideoUtils.getFrameCount(file)

    @UiThread
    fun tick(message: String) {
        val frameIndex = VideoUtils.getFrameIndex(message)
        if (frameIndex < 0) {
            return
        }

        if (progresses.size > COUNT) {
            progresses.removeFirst()
        }

        progresses.addLast(Progress(frameIndex))
    }

    /**
     * 帧/秒
     */
    private val speed: Double
        get() {
            if (progresses.size <= 1) {
                return -1.0
            }

            return 1000.0 * (progresses.last.frame - progresses.first.frame) / (progresses.last.time - progresses.first.time)
        }

    val progress: Double
        get() {
            if (progresses.isEmpty()) {
                return -1.0
            }

            return 1.0 * progresses.last.frame / totalFrame
        }

    val leftTime: Int
        get() {
            if (progresses.isEmpty()) {
                return -1
            }

            return ((totalFrame - progresses.last.frame) / speed).toInt()
        }

    /**
     * 数组靠前的元素是旧的进度信息
     */
    private val progresses = LinkedList<Progress>()

    private companion object {
        const val COUNT = 5
    }

    private data class Progress(val frame: Int, val time: Long = System.currentTimeMillis())

}