package com.example.vidme.presentation.callback

import com.example.vidme.domain.pojo.YoutubePlaylistInfo

interface PlaylistDownloadState {
    fun onDownloading(
        playlistInfo: YoutubePlaylistInfo,
        index: Int,
        progress: Float,
        timeRemaining: String,
    )

    fun onFinished()
    fun onError(error: String)
}