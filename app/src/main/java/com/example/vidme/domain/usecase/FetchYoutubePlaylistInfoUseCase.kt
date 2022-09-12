package com.example.vidme.domain.usecase

import com.example.vidme.domain.DataState
import com.example.vidme.domain.pojo.YoutubePlaylistInfo
import javax.inject.Inject

class FetchYoutubePlaylistInfoUseCase @Inject constructor() : BaseUseCase() {
    suspend operator fun invoke(
        playlistName: String,
        url: String,
        onPlaylistInfo: (DataState<YoutubePlaylistInfo>) -> Unit,
    ) {
        repository.fetchYoutubePlaylistInfo(playlistName, url, executor) {
            onPlaylistInfo(it)
        }
    }
}