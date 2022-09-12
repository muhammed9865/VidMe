package com.example.vidme.domain.usecase

import com.example.vidme.VidApplication
import com.example.vidme.domain.DataState
import com.example.vidme.domain.pojo.DownloadInfo
import com.example.vidme.domain.pojo.YoutubePlaylistInfo
import com.example.vidme.domain.repository.MediaRepository
import javax.inject.Inject

class DownloadYoutubePlaylistUseCase @Inject constructor(
    private val repository: MediaRepository,
) {

    suspend operator fun invoke(
        playlistInfo: YoutubePlaylistInfo,
        audioOnly: Boolean,
        onDownloadInfo: (DataState<DownloadInfo>) -> Unit,
    ) {
        val executor = VidApplication.executorService
        repository.downloadPlaylist(playlistInfo, audioOnly, executor, onDownloadInfo)
    }
}