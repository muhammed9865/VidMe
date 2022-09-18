package com.example.vidme.data.request

import com.example.vidme.data.extractor.DownloadInfoExtractor
import com.example.vidme.data.extractor.InfoExtractor
import com.example.vidme.domain.pojo.VideoRequest
import com.example.vidme.domain.util.FileUtil
import javax.inject.Inject

open class VideoDownloadRequest @Inject constructor(
    url: String,
    private val videoRequest: VideoRequest,
) :
    DownloadRequest(url, videoRequest.type == VideoRequest.TYPE_AUDIO) {

    private val extractor: DownloadInfoExtractor by lazy { DownloadInfoExtractor() }

    override fun getOptions(): Map<String, String?> {
        return mapOf(
            "-o" to FileUtil.getStorageUri().toString() + "/%(title)s.%(ext)s",
            "--no-playlist" to null,
            "-f" to videoRequest.buildType()
        )

    }

    override fun getExtractor(): InfoExtractor {
        return extractor
    }

}