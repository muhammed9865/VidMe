package com.example.vidme.data.extractor.video

import com.example.vidme.data.extractor.VideoInfoExtractor
import com.example.vidme.data.pojo.info.VideoInfo

class FacebookVideoInfoExtractor : VideoInfoExtractor{


    override fun extract(lines: List<String>): VideoInfo {
        var info = VideoInfo()
        lines.forEach { line ->
            info = if (line.contains(THUMBNAIL_SLICE)) {
                info.copy(thumbnail = line)
            } else if (line.contains(REMOTE_URL_SLICE)) {
                info.copy(remoteUrl = line)
            } else if (line.isDigitsOnly()) {
                info.copy(id = line)
            } else {
                info.copy(title = line)
            }
        }

        return info
    }

    private fun String.isDigitsOnly(): Boolean {
        return this.all { it.isDigit() }
    }

    companion object {
        private const val THUMBNAIL_SLICE = "https://scontent"
        private const val REMOTE_URL_SLICE = "https://video"
    }


}