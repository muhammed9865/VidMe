package com.example.vidme.data.pojo.info

/*
    @param currentVideoIndex: in playlist, the current video being downloaded.
 */
data class DownloadInfo(
    var progress: Float = 0f,
    var timeRemaining: Long = 0,
    val currentVideoIndex: Int = -1,
    val storageLocation: String = "",
) : Info
