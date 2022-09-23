package com.example.vidme.service.audio

sealed interface AudioExtras {
    companion object {
        const val EXTRA_SEEK_TO_POSITION = "seek_to_position"
    }
}