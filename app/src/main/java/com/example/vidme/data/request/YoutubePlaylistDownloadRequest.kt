package com.example.vidme.data.request

import com.example.vidme.data.DataConstants
import com.example.vidme.data.extractor.InfoExtractor
import com.example.vidme.data.extractor.YoutubePlaylistInfoExtractor

open class YoutubePlaylistDownloadRequest constructor(url: String, audioOnly: Boolean = false) :
    DownloadRequest(url, audioOnly) {



    override fun getOptions(): Map<String, String?> {
        return mapOf(
            "-o" to DataConstants.getStorageUri().toString() + "/%(title)s.%(ext)s",
            "-f" to "best",
        )
    }

    override fun getExtractor(): InfoExtractor {
        return YoutubePlaylistInfoExtractor()
    }

}