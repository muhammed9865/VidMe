package com.example.vidme.data.mapper

import com.example.vidme.data.cache.relation.PlaylistWithVideos
import com.example.vidme.data.pojo.cache.YoutubePlaylistInfoCache
import com.example.vidme.data.pojo.info.YoutubePlaylistInfo

fun YoutubePlaylistInfo.toCache(): YoutubePlaylistInfoCache {
    return YoutubePlaylistInfoCache(
        name, originalUrl
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