package com.example.vidme.data.request

import com.example.vidme.data.extractor.InfoExtractor
import com.example.vidme.data.extractor.YoutubePlaylistInfoExtractor
import javax.inject.Inject

open class YoutubePlaylistInfoRequest @Inject constructor(
    private val playlistName: String,
    url: String,
) : DownloadRequest(url) {

    private val extractor: YoutubePlaylistInfoExtractor by lazy {
        YoutubePlaylistInfoExtractor(playlistName = playlistName)
    }

    override fun getOptions(): Map<String, String?> {
        return mapOf(
            "--no-warnings" to null,
            "--get-title" to null,
            "--get-thumbnail" to null,
            "--get-url" to null,
            "--get-id" to null,
            "--get-duration" to null,
            "-f" to "best"
        )
    }

    override fun getExtractor(): InfoExtractor {
        return extractor
    }
}