package com.example.vidme.domain.usecase

import com.example.vidme.VidApplication
import com.example.vidme.domain.DataState
import com.example.vidme.domain.pojo.VideoInfo
import com.example.vidme.domain.repository.MediaRepository
import javax.inject.Inject

class FetchVideoInfoUseCase @Inject constructor(private val repository: MediaRepository) {

    suspend operator fun invoke(url: String, onVideoInfo: (DataState<VideoInfo>) -> Unit) {
        val executor = VidApplication.executorService
        repository.getVideoInfo(url, executor, onVideoInfo)
    }
}