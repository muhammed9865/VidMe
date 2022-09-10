package com.example.vidme.data.cache.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.vidme.data.pojo.cache.YoutubePlaylistInfoCache
import com.example.vidme.data.pojo.info.VideoInfo

data class PlaylistWithVideos(
    @Embedded
    val playlistInfoCache: YoutubePlaylistInfoCache,
    @Relation(
        parentColumn = "name",
        entityColumn = "playlistName"
    )
    val videos: List<VideoInfo>,
)
