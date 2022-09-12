package com.example.vidme.domain.usecase

import com.example.vidme.domain.pojo.YoutubePlaylistWithVideos
import javax.inject.Inject


class GetStoredPlaylistUseCase @Inject constructor() : BaseUseCase() {

    suspend operator fun invoke(playlistName: String): YoutubePlaylistWithVideos {
        return repository.getStoredYoutubePlaylistByName(playlistName)
    }
}