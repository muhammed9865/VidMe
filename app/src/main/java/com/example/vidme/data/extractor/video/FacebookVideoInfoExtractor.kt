package com.example.vidme.data.extractor.video

import com.example.vidme.data.extractor.VideoInfoExtractor
import com.example.vidme.data.pojo.info.VideoInfo

class FacebookVideoInfoExtractor : VideoInfoExtractor{


    override fun extract(lines: List<String>): VideoInfo {
        var info = VideoInfo()
        lines.forEach { line ->
            info = if (line)
        }

        return info
    }
}