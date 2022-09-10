package com.example.vidme.data.extractor

import com.example.vidme.data.pojo.info.DownloadInfo
import javax.inject.Inject

class DownloadInfoExtractor @Inject constructor() : InfoExtractor {

    fun extract(progress: Float, timeRemaining: Long, lines: Map<Int, String>): DownloadInfo {
        var info = DownloadInfo()

        try {
            lines.values.first { line -> line.contains("Destination") }.let {
                info = info.copy(storageLocation = getStorageLocation(it))
            }

            lines.values.last { line -> line.contains("Downloading video") }.let {
                info = info.copy(currentVideoIndex = getCurrentVideoIndexFromLine(it))
            }
        } catch (e: Exception) {

        }


        info.progress = progress
        info.timeRemaining = timeRemaining


        return info
    }

    private fun getStorageLocation(line: String) = line.substringAfter("Destination: ").trim()
    private fun getCurrentVideoIndexFromLine(line: String): Int {
        val subString = line.substringAfter("Downloading video ")
        // subtracting 1 because line pattern is not zero-indexed ex: first is "1 of 3"
        return subString[0].digitToInt() - 1
    }
}