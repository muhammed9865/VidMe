package com.example.vidme.data.mapper

import com.example.vidme.data.pojo.info.DownloadInfo

fun DownloadInfo.toDomain(): com.example.vidme.domain.pojo.DownloadInfo {
    return com.example.vidme.domain.pojo.DownloadInfo(
        progress,
        timeRemaining,
        currentVideoIndex
    )
}