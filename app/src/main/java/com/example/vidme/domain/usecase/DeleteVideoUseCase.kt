package com.example.vidme.domain.usecase

import com.example.vidme.domain.pojo.VideoInfo
import javax.inject.Inject

class DeleteVideoUseCase @Inject constructor() : BaseUseCase() {

    suspend operator fun invoke(videoInfo: VideoInfo): Boolean {
        return repository.deleteVideo(videoInfo)
    }
}