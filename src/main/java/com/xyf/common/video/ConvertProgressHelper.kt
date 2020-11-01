package com.xyf.common.video

import java.io.File
import java.util.*

class ConvertProgressHelper(private val file: File) {

    private val totalFrame = VideoUtils.getFrameCount(file)

    fun tick(message: String) {
        if (progresses.size > COUNT) {
            progresses.removeFirst()
        }

        progresses.addLast(Progress(VideoUtils.getFrameIndex(message)))
    }

    /**
     * 帧/秒
     */
    val speed: Double
        get() {
            if (progresses.size < COUNT) {
                return 0.0
            }

            return 1000.0 * (progresses.last.frame - progresses.first.frame) / (progresses.last.time - progresses.first.time)
        }

    /**
     * 数组靠前的元素是旧的进度信息
     */
    private val progresses = LinkedList<Progress>()

    private companion object {
        const val COUNT = 10
    }

    private data class Progress(val frame: Int, val time: Long = System.currentTimeMillis())

}