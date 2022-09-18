package com.example.vidme.domain.pojo

import com.example.vidme.domain.util.StringUtil.calculateDuration
import com.example.vidme.domain.util.StringUtil.durationAsString

data class YoutubePlaylistWithVideos(
    val playlistInfo: YoutubePlaylistInfo,
    val videos: List<VideoInfo>,
) {
    fun getPlaylistName() = playlistInfo.name
    fun getCount() = playlistInfo.count
    fun getOriginalUrl() = playlistInfo.originalUrl
    fun getFullDuration() = durationAsString(videos.sumOf { calculateDuration(it.duration) })



}