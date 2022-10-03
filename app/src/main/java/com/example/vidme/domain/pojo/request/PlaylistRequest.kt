package com.example.vidme.domain.pojo.request

import com.example.vidme.domain.pojo.YoutubePlaylistInfo

class PlaylistRequest(
    val playlistInfo: YoutubePlaylistInfo?,
    type: String = TYPE_AUDIO,
    quality: String = QUALITY_BEST,
) : Request(type, quality) {
    companion object {
        fun emptyRequest() = PlaylistRequest(null)
    }
}
