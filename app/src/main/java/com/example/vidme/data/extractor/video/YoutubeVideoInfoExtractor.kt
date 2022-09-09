package com.example.vidme.data.extractor.video

import com.example.vidme.data.pojo.info.VideoInfo
import javax.inject.Inject

class YoutubeVideoInfoExtractor @Inject constructor(): VideoInfoExtractor {

    override fun extract(lines: List<String>): VideoInfo {
        var info = VideoInfo()
        lines.forEach { line ->
            info = if (line.contains(THUMBNAIL_SLICE)) {
                info.copy(thumbnail = line)
            } else if (line.contains(REMOTE_URL_SLICE)) {
                info.copy(remoteUrl = line)
            } else if (line.length == VIDEO_ID_LENGTH) {
                info.copy(id = line)
            } else {
                info.copy(title = line)
            }
        }

        return info
    }

    companion object {
        private const val VIDEO_ID_LENGTH = 11
        private const val THUMBNAIL_SLICE = "https://i.ytimg"
        private const val REMOTE_URL_SLICE = "https://rr"
    }
}