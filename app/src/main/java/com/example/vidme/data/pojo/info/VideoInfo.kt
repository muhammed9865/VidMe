package com.example.vidme.data.pojo.info

import androidx.room.ColumnInfo
import androidx.room.Entity

/*
    @param id: Video id
    @param originalUrl: the original url that user inputted it
    @param remoteUrl: the url of the video fetched "Online and not downloaded"
    @param storageUrl: the url of the video on-device "Downloaded"
    @param playlistName: if the video is part of a playlist. Used in SQL Relations
 */
@Entity(tableName = "videos_table", primaryKeys = ["id", "playlistName"])
data class VideoInfo(
    val id: String = "",
    val title: String = "",
    val originalUrl: String = "",
    val remoteUrl: String = "",
    val thumbnail: String = "",
    @ColumnInfo(defaultValue = "")
    val duration: String = "",
    var isVideo: Boolean = false,
    var isAudio: Boolean = false,
    var storageUrl: String? = null,
    val playlistName: String = "",
) : Info {

    fun isDownloaded() = storageUrl != null

    fun updateStorageUrl(url: String) {
        storageUrl = url
    }

}
