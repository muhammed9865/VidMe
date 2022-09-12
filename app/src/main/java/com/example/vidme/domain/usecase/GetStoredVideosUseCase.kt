package com.example.vidme.domain.usecase

import javax.inject.Inject

class GetStoredVideosUseCase @Inject constructor() : BaseUseCase() {

    suspend operator fun invoke() = repository.getStoredVideos()
}