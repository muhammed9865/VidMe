package com.example.vidme.domain.pojo.request

abstract class Request(
    open val type: String = TYPE_AUDIO,
    open val quality: String = QUALITY_BEST,
) {
    fun getMediaType(): String {
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
        private const val QUALITY_BEST_AR: String = "الأفضل"
        private const val QUALITY_WORST = "worst"
        private const val QUALITY_WORST_AR = "الاسوأ"

        fun typeFromString(string: String): String {
            return when (string.lowercase()) {
                in audioTypes -> TYPE_AUDIO
                in videoTypes -> TYPE_VIDEO
                else -> throw IllegalStateException("$string is not a valid type, type must be one of ${audioTypes + videoTypes}")
            }
        }

        fun qualityFromString(string: String): String {
            return when (string.lowercase()) {
                in qualityBest -> QUALITY_BEST
                in qualityWorst -> QUALITY_WORST
                else -> throw IllegalStateException("$string is not a valid quality, quality must be one of ${qualityBest + qualityWorst}")
            }
        }

        private val audioTypes = arrayOf(
            TYPE_AUDIO,
            TYPE_AUDIO_AR,
        )

        private val videoTypes = arrayOf(
            TYPE_VIDEO,
            TYPE_VIDEO_AR,
        )

        private val qualityBest = arrayOf(
            QUALITY_BEST,
            QUALITY_BEST_AR
        )

        private val qualityWorst = arrayOf(
            QUALITY_WORST,
            QUALITY_WORST_AR
        )

    }
}