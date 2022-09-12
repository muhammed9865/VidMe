package com.example.vidme.domain.usecase

import com.example.vidme.domain.DataState
import com.example.vidme.domain.pojo.DownloadInfo
import com.example.vidme.domain.pojo.YoutubePlaylistInfo

class DownloadYoutubePlaylistUseCase : BaseUseCase() {

    suspend operator fun invoke(
        playlistInfo: YoutubePlaylistInfo,
        audioOnly: Boolean,
        onDownloadInfo: (DataState<DownloadInfo>) -> Unit,
    ) {
        repository.downloadPlaylist(playlistInfo, audioOnly, executor, onDownloadInfo)
    }

}