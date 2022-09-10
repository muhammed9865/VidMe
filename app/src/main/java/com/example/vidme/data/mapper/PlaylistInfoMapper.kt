package com.example.vidme.data.mapper

import com.example.vidme.data.pojo.cache.YoutubePlaylistInfoCache
import com.example.vidme.data.pojo.info.YoutubePlaylistInfo

fun YoutubePlaylistInfo.toCache(): YoutubePlaylistInfoCache {
    return YoutubePlaylistInfoCache(
        name, count, originalUrl
    )
}