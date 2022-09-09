package com.example.vidme.data.extractor

import com.example.vidme.data.pojo.info.DownloadInfo
import javax.inject.Inject

class DownloadInfoExtractor @Inject constructor() : InfoExtractor {

    fun extract(videoId: String, progress: Float, timeRemaining: Long, lines: Map<Int, String>): DownloadInfo {
        var info = DownloadInfo()

        lines.values.first { line -> line.contains("Destination") }.let {
            info = info.copy(storageLocation = getStorageLocation(it))
        }

        info.progress = progress
        info.timeRemaining = timeRemaining
        info = info.copy(videoId = videoId)

        return info
    }

    private fun getStorageLocation(line: String) = line.substringAfter("Destination: ").trim()
}