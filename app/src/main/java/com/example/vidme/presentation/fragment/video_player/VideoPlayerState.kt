package com.example.vidme.presentation.fragment.video_player

data class VideoPlayerState(
    val currentPosition: Int = -1,
    val title: String = "",
    val videoPath: String? = null,
)