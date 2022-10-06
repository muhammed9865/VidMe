package com.example.vidme.data.extractor.video

import com.example.vidme.data.extractor.InfoExtractor
import com.example.vidme.data.pojo.info.Info
import com.example.vidme.data.pojo.info.VideoInfo
import com.example.vidme.domain.util.StringUtil
import javax.inject.Inject

class FacebookVideoInfoExtractor @Inject constructor() : InfoExtractor {


    override fun extract(originalUrl: String, outputLines: Map<Int, String>): Info {
        var info = VideoInfo(originalUrl = originalUrl)

        outputLines.values.forEach { line ->
            info = if (line.contains(THUMBNAIL_SLICE)) {
                info.copy(thumbnail = line)
            } else if (line.contains(REMOTE_URL_SLICE)) {
                info.copy(remoteUrl = line)
            } else if (StringUtil.isDigitsOnly(line) && line.length > 6) {
                info.copy(id = line)

            } else if (StringUtil.containsDuration(line)) {
                info.copy(duration = line)
            } else {
                info.copy(title = line)
            }
        }

        return info
    }


    companion object {
        private const val THUMBNAIL_SLICE = "https://scontent"
        private const val REMOTE_URL_SLICE = "https://video"
    }


}