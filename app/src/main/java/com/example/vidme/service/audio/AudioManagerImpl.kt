package com.example.vidme.service.audio

import android.content.Intent
import android.media.MediaPlayer
import com.example.vidme.domain.pojo.VideoInfo
import com.example.vidme.service.AudioService.Companion.EXTRA_PLAYLIST
import com.example.vidme.service.AudioService.Companion.EXTRA_VIDEO_INFO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class AudioManagerImpl @Inject constructor() : AudioManager, MediaPlayer.OnCompletionListener {

    private var mediaPlayer: MediaPlayer? = null

    private val trackList = mutableListOf<VideoInfo>()
    private var currentTrackIndex = 0
    private var currentPosition = 0

    private val audioListeners = mutableMapOf<Int, AudioDataListener>()


    override fun init() {
        if (mediaPlayer == null)
            mediaPlayer = MediaPlayer().apply {
                setOnCompletionListener(this@AudioManagerImpl)
            }
    }

    override fun getFromIntent(intent: Intent?) {
        if (intent?.hasExtra(EXTRA_VIDEO_INFO) == true) {
            val videoInfo = intent.extras?.getParcelable<VideoInfo>(EXTRA_VIDEO_INFO)
            videoInfo?.let {
                addTrack(it)
                currentTrackIndex = trackList.lastIndex
            }
            return
        }

        if (intent?.hasExtra(EXTRA_PLAYLIST) == true) {
            val playlist = intent.extras?.getParcelableArrayList<VideoInfo>(EXTRA_PLAYLIST)
            playlist?.let {
                trackList.clear()
                trackList.addAll(it)
                currentTrackIndex = 0
            }
            return
        }
    }

    private fun addTrack(videoInfo: VideoInfo) {
        if (!trackList.contains(videoInfo)) {
            trackList.add(videoInfo)
        } else {
            val index = trackList.indexOf(videoInfo)
            trackList.removeAt(index)
            trackList.add(videoInfo)
        }
    }


    override fun play() {
        val currAudio = trackList[currentTrackIndex]
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        mediaPlayer?.setDataSource(currAudio.url)
        mediaPlayer?.prepare()
        mediaPlayer?.start()
        sendAudioData()
    }

    override fun pause() {
        currentPosition = mediaPlayer?.currentPosition ?: 0
        mediaPlayer?.pause()
    }

    override fun pauseResume() {
        if (isPlaying() == true) {
            pause()
        } else
            resume()
        emitAudioData()
    }

    override fun resume() {
        mediaPlayer?.seekTo(currentPosition)
        mediaPlayer?.start()
        sendAudioData()
    }

    override fun getCurrentVideoInfo(): VideoInfo {
        return trackList[currentTrackIndex]
    }

    override fun next() {
        if (currentTrackIndex + 1 > trackList.size - 1) {
            currentTrackIndex = 0
        } else {
            currentTrackIndex++
        }
        play()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    override fun seekTo(pos: Int) {
        currentPosition = pos
        mediaPlayer?.seekTo(currentPosition)
        Timber.d(mediaPlayer?.currentPosition.toString())
    }

    override fun getVideoDuration(): Long {
        return mediaPlayer?.duration?.toLong() ?: 0
    }

    override fun previous() {
        if (currentTrackIndex - 1 < 0) {
            currentTrackIndex = trackList.size - 1
        } else {
            currentTrackIndex--
        }
        play()
    }

    override fun onDestroy() {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun isPlaying() = mediaPlayer?.isPlaying

    override fun setOnAudioDataListener(requestCode: Int, listener: AudioDataListener) {
        audioListeners[requestCode] = listener
    }

    private fun sendAudioData() {

        if (audioListeners.isNotEmpty()) {
            CoroutineScope(Dispatchers.Main).launch {
                while (isPlaying() == true) {
                    emitAudioData()
                    delay(1000)
                }
            }
        }
    }

    private fun emitAudioData() {
        audioListeners.values.forEach {
            it.invoke(getAudioData())
        }
    }

    override fun getAudioData(): AudioManager.AudioData {
        val single = trackList[currentTrackIndex]
        val duration = mediaPlayer?.duration ?: 0
        val progress = mediaPlayer?.currentPosition ?: 0
        return AudioManager.AudioData(single, progress, duration, isPlaying() ?: false)
    }


    override fun onCompletion(p0: MediaPlayer?) {
        next()
    }
}