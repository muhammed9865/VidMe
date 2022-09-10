package com.example.vidme.data.pojo.cache

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "playlists_table")
data class YoutubePlaylistInfoCache(
    @PrimaryKey
    val name: String = "",
    var count: Int = -1,
    val originalUrl: String = "",
)
