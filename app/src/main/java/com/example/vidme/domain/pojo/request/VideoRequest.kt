package com.example.vidme.domain.pojo.request

import com.example.vidme.domain.pojo.VideoInfo

data class VideoRequest(
    val videoInfo: VideoInfo?,
    override val type: String = TYPE_VIDEO,
    override val quality: String = QUALITY_BEST,
) : Request(type, quality) {

    companion object {
        fun emptyRequest(): VideoRequest {
            return VideoRequest(null)
        }
    }
}
