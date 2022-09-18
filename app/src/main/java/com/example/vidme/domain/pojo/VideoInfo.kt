package com.example.vidme.domain.pojo

data class VideoInfo(
    val id: String,
    val title: String,
    val url: String,
    val thumbnail: String,
    val duration: String,
    val isAudio: Boolean,
    val isVideo: Boolean,
    val isDownloaded: Boolean,
    val playlistName: String?,
    val isDownloading: Boolean = false,
    val downloadInfo: DownloadInfo? = null,
)
