package com.example.vidme.data.request

import com.example.vidme.data.extractor.InfoExtractor
import com.example.vidme.data.extractor.video.FacebookVideoInfoExtractor
import com.example.vidme.data.extractor.video.YoutubeVideoInfoExtractor

class VideoInfoRequest(url: String) : DownloadRequest(url) {
    override fun getOptions(): Map<String, String?> {
        return mapOf(
            "--no-playlist" to null,
            "--get-title" to null,
            "--get-thumbnail" to null,
            "--get-url" to null,
            "--get-id" to null,
            "--get-duration" to null,
            "-f" to "best"
        )
    }

    override fun getExtractor(): InfoExtractor {
        val isYoutube = youtubeReferences.any {
            url.contains(it)
        }
        return if (isYoutube)
            YoutubeVideoInfoExtractor()
        else
            FacebookVideoInfoExtractor()
    }

    companion object {
        private val youtubeReferences = listOf(
            "youtube",
            "youtu.be",
            "https://youtu"
        )
    }
}