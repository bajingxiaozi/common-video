package com.xyf.common.video

import com.google.gson.Gson
import com.xyf.common.annotation.WorkThread
import com.xyf.common.util.SystemUtils2
import com.xyf.common.video.bean.VideoInfoBean
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.util.*
import java.util.regex.Pattern

object FfmpegUtils {

    @WorkThread
    @Throws(Exception::class)
    fun getInfo(video: File?): VideoInfoBean {
        val result = SystemUtils2.execute("ffprobe", "-v", "quiet", "-print_format", "json", "-show_format", "-show_streams", video!!.absolutePath)
        return Gson().fromJson(StringUtils.join<Any>(*result.toTypedArray()), VideoInfoBean::class.java)
    }

    @WorkThread
    @Throws(Exception::class)
    fun getFrameCount(video: File): Int {
        val videoInfo = getInfo(video)
        val duration = videoInfo.format!!.duration!!.toDouble()
        val fpsString = videoInfo.streams!![0]!!.rFrameRate
        val matcher = FPS_PATTERN.matcher(fpsString)
        if (matcher.matches()) {
            val fps = matcher.group("a").toDouble() / matcher.group("b").toDouble()
            return (fps * duration).toInt()
        }
        throw Exception("error video info: $video")
    }

    private val FPS_PATTERN = Pattern.compile("^(?<a>[0-9]+)/(?<b>[0-9]+)$")

    @WorkThread
    fun videoFilter(video: File, scale: Int): List<String> {
        return Arrays.asList("com/xyf/common/video", "-i", video.absolutePath, "-vf", "scale=$scale:$scale,transpose=1")
    }

    private val FRAME_PATTERN = Pattern.compile("^frame=(?<frame>[ 0-9]+)fps.*")

    fun parseFrame(message: String?): Int {
        val matcher = FRAME_PATTERN.matcher(message)
        if (matcher.matches()) {
            val frame = matcher.group("frame")
            return frame.trim { it <= ' ' }.toInt()
        }
        return -1
    }

}