package com.example.vidme.presentation.fragment.single.single_download

import androidx.lifecycle.ViewModel
import com.example.vidme.domain.pojo.VideoInfo
import com.example.vidme.domain.pojo.VideoRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SingleDownloadViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(SingleDownloadState())
    val state get() = _state.asStateFlow()

    private var videoRequest: VideoRequest = VideoRequest.emptyRequest()

    fun getVideoRequest() = videoRequest

    fun setDownloadType(type: String) {
        val downloadType =
            if (type.lowercase() == VideoRequest.TYPE_AUDIO) VideoRequest.TYPE_AUDIO else VideoRequest.TYPE_VIDEO
        videoRequest = videoRequest.copy(type = downloadType)
    }

    fun setVideoInfo(videoInfo: VideoInfo) {
        videoRequest = videoRequest.copy(videoInfo = videoInfo)
    }

    fun setDownloadQuality(quality: String) {
        val downloadQuality =
            if (quality.lowercase() == VideoRequest.QUALITY_BEST) VideoRequest.QUALITY_BEST else VideoRequest.QUALITY_WORST
        videoRequest = videoRequest.copy(quality = downloadQuality)
    }

    fun finishSelection() {
        if (videoRequest.type.isEmpty()) {
            _state.update {
                it.copy(error = "Please select type")
            }
            return
        }

        if (videoRequest.quality.isEmpty()) {
            _state.update {
                it.copy(error = "Please select quality")
            }
            return
        }

        _state.update {
            it.copy(downloadClicked = true)
        }
    }

}