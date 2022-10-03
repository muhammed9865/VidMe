package com.example.vidme.domain.usecase

import com.example.vidme.domain.DataState
import com.example.vidme.domain.pojo.DownloadInfo
import com.example.vidme.domain.pojo.request.PlaylistRequest
import javax.inject.Inject

class DownloadYoutubePlaylistUseCase @Inject constructor() : BaseUseCase() {

    suspend operator fun invoke(
        playlistRequest: PlaylistRequest,
        onDownloadInfo: (DataState<DownloadInfo>) -> Unit,
    ) {
        repository.downloadPlaylist(playlistRequest, executor, onDownloadInfo)
    }

}