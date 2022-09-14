package com.example.vidme.data.mapper

import com.example.vidme.data.pojo.info.DownloadInfo
import com.example.vidme.domain.pojo.VideoInfo

fun DownloadInfo.toDomain(videoInfo: VideoInfo? = null): com.example.vidme.domain.pojo.DownloadInfo {
    return com.example.vidme.domain.pojo.DownloadInfo(
        progress,
        timeRemaining,
        currentVideoIndex,
        isFinished,
        videoInfo
    )
}