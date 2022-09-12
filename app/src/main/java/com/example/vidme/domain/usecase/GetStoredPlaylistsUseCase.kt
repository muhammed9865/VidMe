package com.example.vidme.domain.usecase

import com.example.vidme.domain.pojo.YoutubePlaylistInfo
import javax.inject.Inject

class GetStoredPlaylistsUseCase @Inject constructor() : BaseUseCase() {

    suspend operator fun invoke(): List<YoutubePlaylistInfo> {
        return repository.getStoredYoutubePlaylists()
    }
}