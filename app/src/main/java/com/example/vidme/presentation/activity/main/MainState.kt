package com.example.vidme.presentation.activity.main

data class MainState(
    val syncing: Boolean = false,
    val fetchedPlaylist: Boolean = false,
    val fetchedVideo: Boolean = false,
    val deleting: Boolean = false,
    val playlistWasCached: Boolean = false,
    val downloading: Boolean = false,
    val error: String? = null,
    val simpleMessage: String? = null,
)
