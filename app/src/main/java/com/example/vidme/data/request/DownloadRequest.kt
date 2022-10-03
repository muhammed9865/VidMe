package com.example.vidme.data.request

import com.example.vidme.data.extractor.DownloadInfoExtractor
import com.example.vidme.data.extractor.InfoExtractor
import com.yausername.youtubedl_android.YoutubeDLRequest

abstract class DownloadRequest(val url: String, private val audioOnly: Boolean = false) {

    abstract fun getOptions(): Map<String, String?>

    fun getRequest(): YoutubeDLRequest {
        val request = YoutubeDLRequest(url)
        getOptions().onEach { entry ->
            if (entry.value != null) {
                request.addOption(entry.key, entry.value!!)
            } else {
                request.addOption(entry.key)
            }
        }
        request.addOption("--ignore-errors")
        request.addOption("--no-warnings")

        return request
    }

    abstract fun getExtractor(): InfoExtractor

    fun isDownloading() = getExtractor() is DownloadInfoExtractor
}