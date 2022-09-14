package com.example.vidme.presentation.callback

import com.example.vidme.domain.pojo.VideoInfo

interface SingleDownloadState {
    fun onDownloading(videoInfo: VideoInfo, progress: Float, timeRemaining: String)

    fun onFinished(videoInfo: VideoInfo)
}