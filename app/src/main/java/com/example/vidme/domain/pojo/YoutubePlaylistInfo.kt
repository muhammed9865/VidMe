package com.example.vidme.domain.pojo

data class YoutubePlaylistInfo(
    val name: String,
    val count: Int,
    val videos: List<VideoInfo>,
    val originalUrl: String,
)