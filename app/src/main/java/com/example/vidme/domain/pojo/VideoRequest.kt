package com.example.vidme.domain.pojo

data class VideoRequest(
    val videoInfo: VideoInfo?,
    val type: String = TYPE_VIDEO,
    val quality: String = QUALITY_BEST,
) {

    // Returns the quality and type combined
    fun buildType(): String {
        return buildString {
            append(quality)
            if (type == TYPE_AUDIO)
                append(type)
        }
    }

    companion object {
        const val TYPE_AUDIO = "audio"
        const val TYPE_VIDEO = "video"
        const val QUALITY_BEST = "best"
        const val QUALITY_WORST = "worst"

        fun emptyRequest() = VideoRequest(null)
    }
}
