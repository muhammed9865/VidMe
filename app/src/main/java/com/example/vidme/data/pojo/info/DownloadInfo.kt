package com.example.vidme.data.pojo.info

data class DownloadInfo(
    var progress: Float = 0f,
    var timeRemaining: Float = 0f,
    val videoId: String = "",
    val storageLocation: String = ""
) : Info
