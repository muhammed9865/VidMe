package com.example.vidme.domain.usecase

import com.example.vidme.domain.DataState
import com.example.vidme.domain.pojo.VideoInfo
import javax.inject.Inject

class FetchVideoInfoUseCase @Inject constructor() : BaseUseCase() {

    suspend operator fun invoke(url: String, onVideoInfo: (DataState<VideoInfo>) -> Unit) {
        repository.fetchVideoInfo(url, executor, onVideoInfo)
    }
}