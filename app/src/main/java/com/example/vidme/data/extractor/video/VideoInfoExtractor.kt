package com.example.vidme.data.extractor.video

import com.example.vidme.data.pojo.info.VideoInfo

interface VideoInfoExtractor {
    fun extract(lines: List<String>) : VideoInfo

}