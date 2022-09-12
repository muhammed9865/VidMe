package com.example.vidme.domain.usecase

import com.example.vidme.domain.DataState
import com.example.vidme.domain.pojo.DownloadInfo
import com.example.vidme.domain.pojo.VideoInfo
import javax.inject.Inject

class DownloadVideoUseCase @Inject constructor() : BaseUseCase() {

    suspend operator fun invoke(
        videoInfo: VideoInfo,
        audioOnly: Boolean,
        onDownloadInfo: (DataState<DownloadInfo>) -> Unit,
    ) {
        repository.downloadVideo(videoInfo, audioOnly, executor, onDownloadInfo)
    }

}