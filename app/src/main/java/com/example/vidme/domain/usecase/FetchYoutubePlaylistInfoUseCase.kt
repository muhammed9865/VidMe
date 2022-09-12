package com.example.vidme.domain.usecase

import com.example.vidme.VidApplication
import com.example.vidme.data.pojo.info.YoutubePlaylistInfo
import com.example.vidme.domain.DataState
import com.example.vidme.domain.repository.MediaRepository
import javax.inject.Inject

class FetchYoutubePlaylistInfoUseCase @Inject constructor(
    private val repository: MediaRepository,
) {
    suspend operator fun invoke(
        playlistName: String,
        url: String,
        onPlaylistInfo: (DataState<YoutubePlaylistInfo>) -> Unit,
    ) {
        val executor = VidApplication.executorService
        repository.getYoutubePlaylistInfo(playlistName, url, executor) {
            onPlaylistInfo(it)
        }
    }
}