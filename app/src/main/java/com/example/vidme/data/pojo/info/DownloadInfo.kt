package com.example.vidme.data.pojo.info

data class DownloadInfo(
    var progress: Float = 0f,
    var timeRemaining: Long = 0,
    val videoId: String = "",
    val storageLocation: String = "",
) : Info
