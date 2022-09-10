package com.example.vidme.data.pojo.info


data class YoutubePlaylistInfo(
    val name: String = "",
    var count: Int = -1,
    var videos: List<VideoInfo> = emptyList(),
    val originalUrl: String = "",
) : Info {

    fun addVideo(videoInfo: VideoInfo) {
        val newList = videos.toMutableList()
        newList.add(videoInfo)
        videos = newList

        count = videos.size
    }
}
