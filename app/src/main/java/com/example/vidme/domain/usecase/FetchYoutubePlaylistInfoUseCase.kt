package com.example.vidme.domain.usecase

import com.example.vidme.domain.DataState
import com.example.vidme.domain.pojo.YoutubePlaylistInfo

class FetchYoutubePlaylistInfoUseCase : BaseUseCase() {
    suspend operator fun invoke(
        playlistName: String,
        url: String,
        onPlaylistInfo: (DataState<YoutubePlaylistInfo>) -> Unit,
    ) {
        repository.getYoutubePlaylistInfo(playlistName, url, executor) {
            onPlaylistInfo(it)
        }
    }
}