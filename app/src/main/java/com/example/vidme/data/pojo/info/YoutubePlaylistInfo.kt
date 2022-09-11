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

    operator fun plus(other: YoutubePlaylistInfo): YoutubePlaylistInfo {
        return this.copy(count = count + other.count, videos = videos + other.videos)
    }
}
