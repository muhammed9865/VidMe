package com.example.vidme.data.extractor.video

import com.example.vidme.data.extractor.VideoInfoExtractor
import com.example.vidme.data.pojo.info.VideoInfo

class YoutubeVideoInfoExtractor : VideoInfoExtractor {

    override fun extract(lines: List<String>): VideoInfo {
        var info = VideoInfo()
        lines.forEach { line ->
            info = if (line.contains("https://i.ytimg")) {
                info.copy(thumbnail = line)
            } else if (line.contains("https://rr")) {
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
    }
}