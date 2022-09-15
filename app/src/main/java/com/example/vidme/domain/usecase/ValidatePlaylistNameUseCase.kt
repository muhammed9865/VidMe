package com.example.vidme.domain.usecase

import com.example.vidme.domain.DataState
import javax.inject.Inject

class ValidatePlaylistNameUseCase @Inject constructor() : BaseUseCase() {

    suspend operator fun invoke(playlistName: String): DataState<Boolean> {
        if (playlistName.isEmpty()) {
            return DataState.failure("Name can't be empty")
        }

        val isUsed = repository.getStoredYoutubePlaylistByName(playlistName) != null
        if (isUsed) {
            return DataState.failure("Name is already in use")
        }

        return DataState.success(true)
    }
}