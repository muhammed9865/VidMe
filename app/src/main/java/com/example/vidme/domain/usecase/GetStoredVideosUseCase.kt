package com.example.vidme.domain.usecase

class GetStoredVideosUseCase : BaseUseCase() {

    suspend operator fun invoke() = repository.getStoredVideos()
}