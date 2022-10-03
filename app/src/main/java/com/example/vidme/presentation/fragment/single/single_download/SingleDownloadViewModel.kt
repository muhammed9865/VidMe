package com.example.vidme.presentation.fragment.single.single_download

import androidx.lifecycle.ViewModel
import com.example.vidme.domain.pojo.VideoInfo
import com.example.vidme.domain.pojo.request.Request
import com.example.vidme.domain.pojo.request.VideoRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SingleDownloadViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(SingleDownloadState())
    val state get() = _state.asStateFlow()

    private var request: VideoRequest = VideoRequest.emptyRequest()

    fun getRequest(): Request {
        return request
    }

    fun setDownloadType(type: String) {
        val downloadType =
            if (type.lowercase() == Request.TYPE_AUDIO) Request.TYPE_AUDIO else Request.TYPE_VIDEO
        request = request.copy(type = downloadType)
    }

    fun setVideoInfo(videoInfo: VideoInfo) {
        request = request.copy(videoInfo = videoInfo)
    }

    fun setDownloadQuality(quality: String) {
        val downloadQuality =
            if (quality.lowercase() == Request.QUALITY_BEST) Request.QUALITY_BEST else Request.QUALITY_WORST
        request = request.copy(quality = downloadQuality)
    }

    fun finishSelection() {
        if (request.type.isEmpty()) {
            _state.update {
                it.copy(error = "Please select type")
            }
            return
        }

        if (request.quality.isEmpty()) {
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