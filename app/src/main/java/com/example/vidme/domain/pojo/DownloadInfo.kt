package com.example.vidme.domain.pojo

data class DownloadInfo(
    var progress: Float,
    var timeRemaining: Long,
    val currentVideoIndex: Int,
)