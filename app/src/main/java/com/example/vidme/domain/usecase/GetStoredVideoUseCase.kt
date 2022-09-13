package com.example.vidme.domain.usecase

import javax.inject.Inject

class GetStoredVideoUseCase @Inject constructor() : BaseUseCase() {

    suspend operator fun invoke(id: String, playlistName: String = "") =
        repository.getStoredVideo(id, playlistName)
}