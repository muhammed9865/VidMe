package com.example.vidme.presentation.fragment.playlist.playlist_info

import com.example.vidme.domain.pojo.VideoInfo

data class PlaylistInfoState(
    val playedSingleThumbnail: String = "",
    val playlistTitle: String = "",
    val playlistFullDuration: String = "",
    val singleCount: Int = 0,
    val lastSynced: String = "",
    val singles: List<VideoInfo> = emptyList(),
    val error: String = "",
    val showDownloading: Boolean = false,
    val videoBeingDownloaded: VideoInfo? = null,
    val downloadTimeRemaining: String? = null,
    val downloadProgress: Int? = null,

    )
