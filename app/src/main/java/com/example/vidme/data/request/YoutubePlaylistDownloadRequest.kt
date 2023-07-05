package com.example.vidme.data.request

import com.example.vidme.data.extractor.DownloadInfoExtractor
import com.example.vidme.data.extractor.InfoExtractor
import com.example.vidme.domain.pojo.request.PlaylistRequest
import com.example.vidme.domain.util.FileUtil

open class YoutubePlaylistDownloadRequest constructor(
    private val playlistRequest: PlaylistRequest,
) :
    DownloadRequest(playlistRequest.playlistInfo!!.originalUrl, playlistRequest.isAudio()) {


    override fun getOptions(): Map<String, String?> {
        val storageLocation =
            FileUtil.getStorageFileForPlaylist(playlistRequest.playlistInfo!!.name).absolutePath
        return mapOf(
            "-o" to "$storageLocation/%(title)s.%(ext)s",
            "-f" to playlistRequest.getMediaType(),
            "--download-archive" to "$storageLocation/done.txt"

        )
    }

    override fun getExtractor(): InfoExtractor {
        return DownloadInfoExtractor()
    }

}