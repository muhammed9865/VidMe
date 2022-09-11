package com.example.vidme.data.request

import com.example.vidme.data.extractor.DownloadInfoExtractor
import com.example.vidme.data.extractor.InfoExtractor
import com.example.vidme.domain.util.FileUtil
import javax.inject.Inject

open class VideoDownloadRequest @Inject constructor(url: String, audioOnly: Boolean = false) :
    DownloadRequest(url, audioOnly) {

    private val extractor: DownloadInfoExtractor by lazy { DownloadInfoExtractor() }

    override fun getOptions(): Map<String, String?> {
        return mapOf(
            "-o" to FileUtil.getStorageUri().toString() + "/%(title)s.%(ext)s",
            "--no-playlist" to null,
            "-f" to "best"
        )

    }

    override fun getExtractor(): InfoExtractor {
        return extractor
    }

}