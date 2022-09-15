package com.example.vidme.domain.usecase

import com.example.vidme.domain.DataState
import javax.inject.Inject

class ValidatePlaylistUrlUseCase @Inject constructor() : BaseUseCase() {

    operator fun invoke(url: String): DataState<Boolean> {
        if (url.isEmpty()) {
            return DataState.failure("URL can't be empty")
        }
        if (!url.contains("youtube.com")) {
            return DataState.failure("Only Youtube playlists can be added")
        }

        return DataState.success(true)
    }
}