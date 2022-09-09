package com.example.vidme.data.pojo.info

/*
    @param id: Video id
    @param originalUrl: the original url that user inputted it
    @param remoteUrl: the url of the video fetched "Online and not downloaded"
    @param storageUrl: the url of the video on-device "Downloaded"
 */
data class VideoInfo(
    val id: String = "",
    val title: String = "",
    val originalUrl: String = "",
    val remoteUrl: String = "",
    val thumbnail: String = "",
    var isVideo: Boolean = false,
    var isAudio: Boolean = false,
    var storageUrl: String? = null,
) : Info {

    fun isDownloaded() = storageUrl != null

    fun updateStorageUrl(url: String) {
        storageUrl = url
    }

}
