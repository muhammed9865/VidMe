package com.example.vidme.data.mapper

import com.example.vidme.data.cache.relation.PlaylistWithVideos
import com.example.vidme.data.pojo.cache.YoutubePlaylistInfoCache
import com.example.vidme.data.pojo.info.YoutubePlaylistInfo
import com.example.vidme.domain.pojo.YoutubePlaylistWithVideos
import java.text.SimpleDateFormat
import java.util.*

fun YoutubePlaylistInfo.toCache(): YoutubePlaylistInfoCache {
    val date = SimpleDateFormat("dd/MM/yyyy hh:mm a",
        Locale.getDefault()).format(System.currentTimeMillis())
    return YoutubePlaylistInfoCache(
        name, originalUrl, count, date
    )
}

fun YoutubePlaylistInfoCache.toDomain(): com.example.vidme.domain.pojo.YoutubePlaylistInfo {
    return com.example.vidme.domain.pojo.YoutubePlaylistInfo(
        name,
        count,
        originalUrl,
        lastSynced
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



fun PlaylistWithVideos.toDomain(): YoutubePlaylistWithVideos {
    return YoutubePlaylistWithVideos(
        playlistInfo = playlistInfoCache.toDomain(),
        videos = videos.map { it.toDomain() }
    )
}