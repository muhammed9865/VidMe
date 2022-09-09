package com.example.vidme.data.request

import com.yausername.youtubedl_android.YoutubeDLRequest

interface DownloadRequest {

    fun getOptions(): Map<String, String?>

    fun getRequest(url: String, audioOnly: Boolean) : YoutubeDLRequest {

        val request = YoutubeDLRequest(url)
        getOptions().onEach { entry ->
            if (entry.value != null) {
                request.addOption(entry.key, entry.value!!)
            } else {
                request.addOption(entry.key)
            }
        }

        if (audioOnly) {
            request.addOption("-f", "bestaudio")
        }

        return request
    }
}