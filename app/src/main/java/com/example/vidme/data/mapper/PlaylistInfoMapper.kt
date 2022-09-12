package com.example.vidme.data.mapper

import com.example.vidme.data.cache.relation.PlaylistWithVideos
import com.example.vidme.data.pojo.cache.YoutubePlaylistInfoCache
import com.example.vidme.data.pojo.info.YoutubePlaylistInfo
import com.example.vidme.domain.pojo.YoutubePlaylistWithVideos

fun YoutubePlaylistInfo.toCache(): YoutubePlaylistInfoCache {
    return YoutubePlaylistInfoCache(
        name, originalUrl, count
    )
}

fun YoutubePlaylistInfoCache.toDomain(): com.example.vidme.domain.pojo.YoutubePlaylistInfo {
    return com.example.vidme.domain.pojo.YoutubePlaylistInfo(
        name,
        count,
        originalUrl
    )
}

fun PlaylistWithVideos.toYoutubePlaylistInfo(): YoutubePlaylistInfo {
    return YoutubePlaylistInfo(
        name = playlistInfoCache.name,
        count = videos.size,
        originalUrl = playlistInfoCache.originalUrl,
        videos = videos
    )
}

fun YoutubePlaylistInfo.toDomain(): com.example.vidme.domain.pojo.YoutubePlaylistInfo {
    return com.example.vidme.domain.pojo.YoutubePlaylistInfo(
        name,
        count,
        originalUrl
    )
}

fun PlaylistWithVideos.toDomain(): YoutubePlaylistWithVideos {
    val playlistInfo = toYoutubePlaylistInfo()
    return YoutubePlaylistWithVideos(
        playlistInfo = playlistInfo.toDomain(),
        videos = videos.map { it.toDomain() }
    )
}