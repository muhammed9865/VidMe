package com.example.vidme.presentation.fragment.audio_player

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.example.vidme.domain.util.StringUtil
import com.example.vidme.service.AudioService
import com.example.vidme.service.audio.AudioManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AudioPlayerViewModel @Inject constructor(
    private val audioManager: AudioManager,
) : ViewModel() {

    /* Will be set to null whenever the fragment is destroyed */
    @SuppressLint("StaticFieldLeak")
    private var mService: AudioService? = null

    private val _state = MutableStateFlow(AudioPlayerState())
    val state get() = _state.asStateFlow()

    private var isPlaying = true

    init {
        doOnAudioStateChange()
    }

    fun setService(service: AudioService?) {
        mService = service
    }

    private fun doOnAudioStateChange() {
        audioManager.setOnAudioDataListener { data ->
            updateState(data)
        }
    }

    private fun updateState(data: AudioManager.AudioData) {
        _state.update {
            it.copy(isPlaying = data.isPlaying,
                audioThumbnail = data.single.thumbnail,
                audioTitle = data.single.title,
                audioDuration = data.single.duration,
                audioProgress = StringUtil.durationFromMilli(data.progress),
                audioProgressPercent = data.progress.toFloat() / data.duration.toFloat()
            )
        }
    }

    fun setProgressPercent(percent: Float) {
        val data = audioManager.getAudioData()
        val progress = data.duration * percent
        audioManager.seekTo(progress.toInt())
    }

    fun nextAudio() {
        mService?.next() ?: _state.update { it.copy(error = "Service not initialized") }
        val data = audioManager.getAudioData()
        updateState(data)
    }

    fun previousAudio() {
        mService?.previous() ?: _state.update { it.copy(error = "Service not initialized") }
        val data = audioManager.getAudioData()
        updateState(data)
    }

    fun pauseResume() {
        if (isPlaying) {
            mService?.pause()
        } else mService?.resume()

        updateState(audioManager.getAudioData())
    }

    fun close() {
        mService?.close()
    }

    override fun onCleared() {
        super.onCleared()
        mService = null
    }


}