package com.example.vidme.data.mapper

import com.example.vidme.data.cache.relation.PlaylistWithVideos
import com.example.vidme.data.pojo.cache.YoutubePlaylistInfoCache
import com.example.vidme.data.pojo.info.YoutubePlaylistInfo

fun YoutubePlaylistInfo.toCache(): YoutubePlaylistInfoCache {
    return YoutubePlaylistInfoCache(
        name, count, originalUrl
    )
}

fun PlaylistWithVideos.toYoutubePlaylistInfo(): YoutubePlaylistInfo {
    return YoutubePlaylistInfo(
        name = playlistInfoCache.name,
        count = playlistInfoCache.count,
        originalUrl = playlistInfoCache.originalUrl,
        videos = videos
    )
}