package com.example.vidme.data.pojo.info

data class PlaylistInfo(
    var count: Int = -1,

    var videos: List<VideoInfo> = emptyList(),
) : Info {

    fun addVideo(videoInfo: VideoInfo) {
        val newList = videos.toMutableList()
        newList.add(videoInfo)
        videos = newList

        count = videos.size
    }
}
