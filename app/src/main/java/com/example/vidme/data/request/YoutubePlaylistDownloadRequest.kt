package com.example.vidme.data.request

import com.example.vidme.data.DataConstants
import com.example.vidme.data.extractor.InfoExtractor
import com.example.vidme.data.extractor.YoutubePlaylistInfoExtractor
import com.example.vidme.data.extractor.video.YoutubeVideoInfoExtractor
import javax.inject.Inject

open class YoutubePlaylistDownloadRequest constructor(url: String, audioOnly: Boolean = false) :
    DownloadRequest(url, audioOnly) {

    @Inject
    protected lateinit var videoExtractor: YoutubeVideoInfoExtractor

    override fun getOptions(): Map<String, String?> {
        return mapOf(
            "-o" to DataConstants.getStorageUri().toString() + "/%(title)s.%(ext)s",
            "-f" to "best",
        )
    }

    override fun getExtractor(): InfoExtractor {
       return YoutubePlaylistInfoExtractor(videoExtractor)
    }

}