package com.example.vidme.domain.usecase

import javax.inject.Inject

class GetStoredVideoByID @Inject constructor() : BaseUseCase() {

    suspend operator fun invoke(id: String) = repository.getStoredVideoByID(id)
}