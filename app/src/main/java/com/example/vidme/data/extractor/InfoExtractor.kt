package com.example.vidme.data.extractor

import com.example.vidme.data.pojo.info.Info
import com.example.vidme.data.pojo.info.VideoInfo

interface InfoExtractor {

    fun extract(lines: Map<Int, String>) : Info {
        return VideoInfo()
    }



}