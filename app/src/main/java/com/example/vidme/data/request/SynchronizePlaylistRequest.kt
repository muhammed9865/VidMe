package com.example.vidme.data.request

import com.example.vidme.data.pojo.info.YoutubePlaylistInfo

class SynchronizePlaylistRequest(private val playlistInfo: YoutubePlaylistInfo) :
    YoutubePlaylistInfoRequest(playlistInfo.name, playlistInfo.originalUrl) {

    override fun getOptions(): Map<String, String?> {
        val options = super.getOptions().toMutableMap()
        val lastVideoNumber = playlistInfo.videos.size
        options["--playlist-start"] = "$lastVideoNumber"
        return options
    }


}