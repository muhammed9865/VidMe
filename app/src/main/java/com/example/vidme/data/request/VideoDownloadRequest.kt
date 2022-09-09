package com.example.vidme.data.request

import com.example.vidme.data.DataConstants

class VideoDownloadRequest : DownloadRequest {

    override fun getOptions(): Map<String, String?> {
        return mapOf(
            "-o" to DataConstants.getStorageUri().toString() + "/%(title)s.%(ext)s",
            "--no-playlist" to null,
            "-f" to "best"
        )

    }

}