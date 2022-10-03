package com.example.vidme.domain.usecase

import com.example.vidme.domain.DataState
import com.example.vidme.domain.pojo.DownloadInfo
import com.example.vidme.domain.pojo.request.VideoRequest
import javax.inject.Inject

class DownloadVideoUseCase @Inject constructor() : BaseUseCase() {

    suspend operator fun invoke(
        videoRequest: VideoRequest,
        onDownloadInfo: (DataState<DownloadInfo>) -> Unit,
    ) {
        repository.downloadVideo(videoRequest, executor, onDownloadInfo)
    }

}