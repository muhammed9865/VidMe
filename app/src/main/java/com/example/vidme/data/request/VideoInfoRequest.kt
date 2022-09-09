package com.example.vidme.data.request

import com.example.vidme.data.extractor.InfoExtractor
import com.example.vidme.data.extractor.video.FacebookVideoInfoExtractor
import com.example.vidme.data.extractor.video.YoutubeVideoInfoExtractor

class VideoInfoRequest(url: String, audioOnly: Boolean) : DownloadRequest(url, audioOnly) {
    override fun getOptions(): Map<String, String?> {
        return mapOf(
            "--no-playlist" to null,
            "--get-title" to null,
            "--get-thumbnail" to null,
            "--get-url" to null,
            "--get-id" to null
        )
    }

    override fun getExtractor(): InfoExtractor {
        return if (url.contains("youtube"))
            YoutubeVideoInfoExtractor()
        else
            FacebookVideoInfoExtractor()
    }
}