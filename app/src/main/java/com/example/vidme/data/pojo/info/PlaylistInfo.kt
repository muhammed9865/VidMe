package com.example.vidme.data.pojo.info

data class PlaylistInfo(
    val count: Int = -1,
    var videos: List<VideoInfo>,
) {
    fun setCount(): PlaylistInfo {
        return this.copy(count = videos.size)
    }

    fun addVideo(videoInfo: VideoInfo) {
        val newList = videos.toMutableList()
        newList.add(videoInfo)
        videos = newList

    }
}
