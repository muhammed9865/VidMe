package com.example.vidme.data.request

import com.example.vidme.data.extractor.DownloadInfoExtractor
import com.example.vidme.data.extractor.InfoExtractor
import com.example.vidme.domain.pojo.request.VideoRequest
import com.example.vidme.domain.util.FileUtil
import javax.inject.Inject

open class VideoDownloadRequest @Inject constructor(
    url: String,
    private val videoRequest: VideoRequest,
) :
    DownloadRequest(url, videoRequest.isAudio()) {

    override fun getOptions(): Map<String, String?> {
        val playlistName = videoRequest.videoInfo!!.playlistName
        val storageLocation =
            if (!playlistName.isNullOrEmpty()) FileUtil.getStorageFileForPlaylist(
                playlistName
            ).absolutePath
            else
                FileUtil.getStorageUri().toString()

        return mapOf(
            "-o" to "$storageLocation/%(title)s.%(ext)s",
            "--no-playlist" to null,
            "-f" to videoRequest.getMediaType()
        )

    }

    override fun getExtractor(): InfoExtractor {
        return DownloadInfoExtractor()
    }

}