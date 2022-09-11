package com.example.vidme.data.request

import com.example.vidme.data.extractor.DownloadInfoExtractor
import com.example.vidme.data.extractor.InfoExtractor
import com.example.vidme.data.pojo.info.YoutubePlaylistInfo
import com.example.vidme.domain.util.FileUtil

open class YoutubePlaylistDownloadRequest constructor(
    private val playlistInfo: YoutubePlaylistInfo,
    url: String,
    audioOnly: Boolean = false,
) :
    DownloadRequest(url, audioOnly) {


    override fun getOptions(): Map<String, String?> {

        return mapOf(
            "-o" to FileUtil.getStorageFileForPlaylist(playlistInfo.name).absolutePath + "/%(title)s.%(ext)s",
            "-f" to "best",
        )
    }

    override fun getExtractor(): InfoExtractor {
        return DownloadInfoExtractor()
    }

}