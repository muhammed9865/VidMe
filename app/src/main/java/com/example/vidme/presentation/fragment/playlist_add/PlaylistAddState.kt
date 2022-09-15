package com.example.vidme.presentation.fragment.playlist_add

data class PlaylistAddState(
    val validPlaylistName: Boolean = false,
    val playlistNameError: String? = null,
    val validUrl: Boolean = false,
    val urlError: String? = null,
    val success: Boolean = false,
)
