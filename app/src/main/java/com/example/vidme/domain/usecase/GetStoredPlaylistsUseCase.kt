package com.example.vidme.domain.usecase

import com.example.vidme.domain.pojo.YoutubePlaylistInfo

class GetStoredPlaylistsUseCase : BaseUseCase() {

    suspend operator fun invoke(): List<YoutubePlaylistInfo> {
        return repository.getStoredYoutubePlaylists()
    }
}