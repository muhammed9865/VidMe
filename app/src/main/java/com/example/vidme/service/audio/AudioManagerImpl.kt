package com.example.vidme.service.audio

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.PowerManager
import com.example.vidme.domain.pojo.VideoInfo
import com.example.vidme.service.AudioService.Companion.EXTRA_PLAYLIST
import com.example.vidme.service.AudioService.Companion.EXTRA_VIDEO_INFO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class AudioManagerImpl @Inject constructor() : AudioManager, MediaPlayer.OnCompletionListener,
    MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private var mediaPlayer: MediaPlayer? = null

    private val trackList = mutableListOf<VideoInfo>()
    private var currentTrackIndex = 0
    private var currentPosition = 0

    private val audioListeners = mutableMapOf<Int, AudioDataListener>()

    // If timeout count reaches trackList last index, then stop the play
    private var timeoutCount = 0

    override fun init(context: Context) {
        if (mediaPlayer == null)
            mediaPlayer = MediaPlayer().apply {
                val attrs = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()

                setAudioAttributes(attrs)
                setOnCompletionListener(this@AudioManagerImpl)
                setOnPreparedListener(this@AudioManagerImpl)
                setOnErrorListener(this@AudioManagerImpl)
                setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)

            }
    }


    override fun getFromIntent(intent: Intent?) {

        if (intent?.hasExtra(EXTRA_VIDEO_INFO) == true) {
            val videoInfo = intent.extras?.getParcelable<VideoInfo>(EXTRA_VIDEO_INFO)
            videoInfo?.let {
                Timber.d("single: ${it.title}")
                addTrack(it)

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
        currentTrackIndex = if (!trackList.contains(videoInfo)) {
            trackList.add(videoInfo)
            trackList.lastIndex
        } else {
            trackList.indexOf(videoInfo)
        }


    }


    override fun play() {
        try {
            Timber.d("Curr index: $currentTrackIndex")
            val currAudio = trackList[currentTrackIndex]
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(currAudio.url)
            mediaPlayer?.prepareAsync()
        } catch (e: IOException) {
            e.printStackTrace()
            timeoutCount++
            if (timeoutCount <= trackList.lastIndex)
                next()
            else
                Timber.e("Couldn't play any track in the track list, timeout:$timeoutCount")
        }

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
        } else
            currentTrackIndex++
        Timber.d("Next index: $currentTrackIndex")
        play()

    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    override fun seekTo(pos: Int) {
        currentPosition = pos
        mediaPlayer?.seekTo(currentPosition)
    }

    override fun getVideoDuration(): Long {
        return mediaPlayer?.duration?.toLong() ?: 0
    }

    override fun previous() {
        if (currentTrackIndex - 1 < 0) {
            currentTrackIndex = trackList.lastIndex
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
        Timber.d("OnCompletion Called")
        next()
    }

    override fun onPrepared(mp: MediaPlayer?) {
        Timber.d("OnPrepared Called")
        mp?.start()
        sendAudioData()
    }

    override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
        return true
    }
}