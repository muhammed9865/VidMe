package com.example.vidme.data.pojo.info

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists_table")
data class YoutubePlaylistInfo(
    @PrimaryKey
    val name: String = "",
    var count: Int = -1,
    var videos: List<VideoInfo> = emptyList(),
    val originalUrl: String = "",
) : Info {

    fun addVideo(videoInfo: VideoInfo) {
        val newList = videos.toMutableList()
        newList.add(videoInfo)
        videos = newList

        count = videos.size
    }
}
