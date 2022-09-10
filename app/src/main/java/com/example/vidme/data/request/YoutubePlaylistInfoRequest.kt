package com.example.vidme.data.request

import com.example.vidme.data.extractor.InfoExtractor
import com.example.vidme.data.extractor.YoutubePlaylistInfoExtractor
import javax.inject.Inject

open class YoutubePlaylistInfoRequest @Inject constructor(url: String) : DownloadRequest(url) {

    private val extractor: YoutubePlaylistInfoExtractor by lazy {
        YoutubePlaylistInfoExtractor()
    }

    override fun getOptions(): Map<String, String?> {
        return mapOf(
            "--get-title" to null,
            "--get-thumbnail" to null,
            "--get-url" to null,
            "--get-id" to null,
            "-f" to "best"
        )
    }

    override fun getExtractor(): InfoExtractor {
        return extractor
    }
}