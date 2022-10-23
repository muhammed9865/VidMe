package com.example.vidme.domain.pojo.request

abstract class Request(
    open val type: String = TYPE_AUDIO,
    open val quality: String = QUALITY_BEST,
) {
    fun buildType(): String {
        return buildString {
            append(quality)
            if (type == TYPE_AUDIO)
                append(type)
        }
    }


    fun isAudio() = type == TYPE_AUDIO

    companion object {
        const val TYPE_AUDIO = "audio"
        private const val TYPE_AUDIO_AR = "صوت"
        const val TYPE_VIDEO = "video"
        private const val TYPE_VIDEO_AR = "فيديو"
        const val QUALITY_BEST = "best"
        const val QUALITY_WORST = "worst"

        fun typeFromString(string: String): String {
            return if (string in audioTypes) TYPE_AUDIO else TYPE_VIDEO
        }

        private val audioTypes = listOf(
            TYPE_AUDIO,
            TYPE_AUDIO_AR,
        )

        private val videoTypes = listOf(
            TYPE_VIDEO,
            TYPE_VIDEO_AR,
        )

    }
}