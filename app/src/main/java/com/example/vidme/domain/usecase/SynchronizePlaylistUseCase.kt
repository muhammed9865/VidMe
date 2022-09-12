package com.example.vidme.domain.usecase

import com.example.vidme.domain.DataState
import com.example.vidme.domain.pojo.YoutubePlaylistInfo
import javax.inject.Inject

class SynchronizePlaylistUseCase @Inject constructor() : BaseUseCase() {

    suspend operator fun invoke(
        playlistInfo: YoutubePlaylistInfo,
        onPlaylistInfo: (DataState<YoutubePlaylistInfo>) -> Unit,
    ) {
        repository.synchronizeYoutubePlaylistInfo(playlistInfo, executor, onPlaylistInfo)
    }
}