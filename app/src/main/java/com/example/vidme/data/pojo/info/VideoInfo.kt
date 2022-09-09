package com.example.vidme.data.pojo.info

/*
    @param id: Video id
 */
data class VideoInfo(
    val id: String = "",
    val title: String = "",
    val remoteUrl: String = "",
    val thumbnail: String = "",
    var storageUrl: String? = null
) : Info {

    fun isDownloaded() = storageUrl != null

    fun updateStorageUrl(url: String) {
        storageUrl = url
    }

}
