package com.example.vidme.data.request

import com.example.vidme.data.pojo.info.YoutubePlaylistInfo

class SynchronizePlaylistRequest(playlistInfo: YoutubePlaylistInfo) :
    YoutubePlaylistInfoRequest(playlistInfo.name, playlistInfo.originalUrl) {


}