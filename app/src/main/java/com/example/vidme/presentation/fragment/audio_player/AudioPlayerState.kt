package com.example.vidme.presentation.fragment.audio_player

data class AudioPlayerState(
    val error: String? = null,
    val isPlaying: Boolean = false,
    val audioThumbnail: String? = null,
    val audioTitle: String? = null,
    val audioDuration: String? = null,
    val audioProgress: String = "",
    val audioProgressPercent: Float = 0.0f,
)