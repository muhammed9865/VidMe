package com.example.vidme.presentation.fragment.video_player

import androidx.lifecycle.ViewModel
import com.example.vidme.domain.pojo.VideoInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(VideoPlayerState())
    val state = _state.asStateFlow()

    private lateinit var video: VideoInfo

    fun setPlaybackState(position: Int, isPlaying: Boolean) {
        _state.update {
            it.copy(currentPosition = position, isPlaying = isPlaying)
        }
    }

    fun setControlsVisibility(visible: Boolean) {
        _state.update {
            it.copy(videoPath = null, showControls = visible)
        }
    }

    fun setVideo(videoInfo: VideoInfo) {
        video = videoInfo
        _state.update {
            it.copy(title = video.title, videoPath = videoInfo.url)
        }
    }

    fun getLastPosition() = _state.value.currentPosition

    fun isPlaying() = _state.value.isPlaying

}