package com.example.vidme.presentation.callback

import com.example.vidme.domain.pojo.DownloadInfo
import com.example.vidme.domain.pojo.VideoInfo

interface SingleDownloadState {
    fun onDownloading(downloadInfo: DownloadInfo)

    fun onFinished(videoInfo: VideoInfo)
}