package com.example.vidme.data.extractor

import com.example.vidme.data.pojo.info.VideoInfo

interface VideoInfoExtractor {
    fun extract(lines: List<String>) : VideoInfo

}