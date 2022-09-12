package com.example.vidme.data.pojo.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "playlists_table")
data class YoutubePlaylistInfoCache(
    @PrimaryKey
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    val name: String = "",
    val originalUrl: String = "",
    val count: Int = -1,
)
