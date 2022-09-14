package com.example.vidme.domain.pojo

data class YoutubePlaylistInfo(
    val name: String,
    val count: Int,
    val originalUrl: String,
    val lastSynced: String,
    val isSyncing: Boolean = false,
)