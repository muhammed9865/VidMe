package com.example.vidme.domain.pojo

data class DownloadInfo(
    var progress: Float,
    var timeRemaining: Long,
    val currentVideoIndex: Int,
    val isFinished: Boolean,
    val videoInfo: VideoInfo? = null,
)