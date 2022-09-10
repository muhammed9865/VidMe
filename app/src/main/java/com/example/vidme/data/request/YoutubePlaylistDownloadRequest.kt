package com.example.vidme.data.request

import com.example.vidme.data.FileUtil
import com.example.vidme.data.extractor.DownloadInfoExtractor
import com.example.vidme.data.extractor.InfoExtractor

open class YoutubePlaylistDownloadRequest constructor(
    private val playlistName: String,
    url: String,
    audioOnly: Boolean = false,
) :
    DownloadRequest(url, audioOnly) {


    override fun getOptions(): Map<String, String?> {

        return mapOf(
            "-o" to FileUtil.getStorageFileForPlaylist(playlistName).absolutePath + "/%(title)s.%(ext)s",
            "-f" to "best",
        )
    }

    override fun getExtractor(): InfoExtractor {
        return DownloadInfoExtractor()
    }

}