package com.example.vidme.data.mapper

import com.example.vidme.data.pojo.info.VideoInfo


fun VideoInfo.toDomain(): com.example.vidme.domain.pojo.VideoInfo {
    val url = if (isDownloaded()) storageUrl!! else remoteUrl
    return com.example.vidme.domain.pojo.VideoInfo(
        id,
        title,
        url,
        duration,
        isAudio,
        isVideo,
        isDownloaded(),
        playlistName
    )
}