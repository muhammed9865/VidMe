package com.example.vidme.data.request

import com.example.vidme.data.DataConstants
import com.example.vidme.data.extractor.InfoExtractor
import com.example.vidme.data.extractor.YoutubePlaylistInfoExtractor
import com.example.vidme.data.extractor.video.YoutubeInfoExtractor
import javax.inject.Inject

open class YoutubePlaylistDownloadRequest @Inject constructor(): DownloadRequest {

    @Inject
    protected lateinit var videoExtractor: YoutubeInfoExtractor

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