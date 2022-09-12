package com.example.vidme.domain.usecase

import com.example.vidme.VidApplication
import com.example.vidme.domain.repository.MediaRepository
import javax.inject.Inject

abstract class BaseUseCase @Inject constructor() {

    @Inject
    protected lateinit var repository: MediaRepository

    protected val executor = VidApplication.executorService
}