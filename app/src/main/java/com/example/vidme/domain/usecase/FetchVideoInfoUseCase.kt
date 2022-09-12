package com.example.vidme.domain.usecase

import com.example.vidme.domain.DataState
import com.example.vidme.domain.pojo.VideoInfo

class FetchVideoInfoUseCase : BaseUseCase() {

    suspend operator fun invoke(url: String, onVideoInfo: (DataState<VideoInfo>) -> Unit) {
        repository.getVideoInfo(url, executor, onVideoInfo)
    }
}