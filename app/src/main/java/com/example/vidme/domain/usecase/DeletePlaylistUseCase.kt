package com.example.vidme.domain.usecase

import javax.inject.Inject

class DeletePlaylistUseCase @Inject constructor() : BaseUseCase() {

    suspend operator fun invoke(playlistName: String): Boolean =
        repository.deletePlaylistByName(playlistName)
}