package com.example.vidme.service.audio

import android.content.Context
import android.content.Intent
import com.example.vidme.domain.pojo.VideoInfo

typealias AudioDataListener = (audioData: AudioManager.AudioData) -> Unit

interface AudioManager {

    fun init(context: Context)
    fun getFromIntent(intent: Intent?)
    fun play()
    fun pause()
    fun resume()
    fun pauseResume()
    fun getCurrentVideoInfo(): VideoInfo
    fun getCurrentPosition(): Int
    fun next()
    fun previous()
    fun onDestroy()
    fun seekTo(pos: Int)
    fun getVideoDuration(): Long
    fun setOnAudioDataListener(requestCode: Int, listener: AudioDataListener)
    fun getAudioData(): AudioData

    data class AudioData(
        val single: VideoInfo,
        val progress: Int,
        val duration: Int,
        val isPlaying: Boolean,
    )
}