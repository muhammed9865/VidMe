package com.example.vidme.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.example.vidme.service.audio.AudioActions
import com.example.vidme.service.audio.AudioExtras
import com.example.vidme.service.audio.AudioManager
import com.example.vidme.service.notification.NotificationManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AudioService : Service() {

    @Inject
    lateinit var audioManager: AudioManager

    @Inject
    lateinit var notificationManager: NotificationManager

    private val binder = ServiceBinder()


    override fun onBind(p0: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.e(intent?.action ?: "Error")
        audioManager.getFromIntent(intent)
        handleIntent(intent)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        audioManager.init(this)

        // Changing the notification details when audioManager changes the single on it's own
        audioManager.setOnAudioDataListener(requestCode = 1010) {
            if (notificationManager.getCurrentVideo().id != it.single.id) {
                notificationManager.setCurrentVideoInfo(it.single)
                notificationManager.show(this)
            }
        }
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action != null) {
            val action = intent.action!!
            Timber.d(action)
            when (action) {
                AudioActions.ACTION_PLAY -> {
                    audioManager.play()
                    val videoInfo = audioManager.getCurrentVideoInfo()
                    notificationManager.setCurrentVideoInfo(videoInfo)
                    notificationManager.setPlayingState(true)
                    startForeground(1337, notificationManager.buildNotification(this))
                    notificationManager.show(this)

                }

                AudioActions.ACTION_PAUSE -> {
                    audioManager.pauseResume()
                    notificationManager.setPlayingState(false)
                    notificationManager.show(this)
                }

                AudioActions.ACTION_NEXT -> {
                    audioManager.next()
                    val nextAudio = audioManager.getCurrentVideoInfo()
                    notificationManager.setCurrentVideoInfo(nextAudio)
                    notificationManager.setPlayingState(true)
                    notificationManager.show(this)
                }

                AudioActions.ACTION_PREV -> {
                    audioManager.previous()
                    val previousVideo = audioManager.getCurrentVideoInfo()
                    notificationManager.setCurrentVideoInfo(previousVideo)
                    notificationManager.setPlayingState(true)
                    notificationManager.show(this)
                }

                AudioActions.ACTION_RESUME -> {
                    audioManager.pauseResume()
                    notificationManager.setPlayingState(true)
                    notificationManager.show(this)
                }

                AudioActions.ACTION_SEEK_TO -> {
                    val position = intent.getIntExtra(AudioExtras.EXTRA_SEEK_TO_POSITION,
                        audioManager.getCurrentPosition())
                    audioManager.seekTo(position)
                    notificationManager.show(this)
                }

                AudioActions.ACTION_STOP -> {
                    stopSelf()
                }

                else -> {
                    audioManager.pauseResume()
                    notificationManager.setPlayingState(false)
                    notificationManager.show(this)
                }
            }
        }
    }

    fun next() {
        val intent = Intent(this, this::class.java).apply {
            action = AudioActions.ACTION_NEXT
        }
        handleIntent(intent)
    }

    fun previous() {
        val intent = Intent(this, this::class.java).apply {
            action = AudioActions.ACTION_PREV
        }
        handleIntent(intent)
    }

    fun pause() {
        val intent = Intent(this, this::class.java).apply {
            action = AudioActions.ACTION_PAUSE
        }
        handleIntent(intent)
    }

    fun resume() {
        val intent = Intent(this, this::class.java).apply {
            action = AudioActions.ACTION_RESUME
        }
        handleIntent(intent)
    }

    fun close() {
        val intent = Intent(this, this::class.java).apply {
            action = AudioActions.ACTION_STOP
        }
        handleIntent(intent)
    }

    fun seekTo(position: Int) {
        val intent = Intent(this, this::class.java).apply {
            action = AudioActions.ACTION_SEEK_TO
            putExtra(AudioExtras.EXTRA_SEEK_TO_POSITION, position)
        }
        handleIntent(intent)
    }


    override fun onDestroy() {
        super.onDestroy()
        audioManager.onDestroy()
        notificationManager.onDestroy()
    }

    companion object {
        const val EXTRA_VIDEO_INFO = "extra_video_info"
        const val EXTRA_PLAYLIST = "extra_playlist"
    }


    inner class ServiceBinder : Binder() {
        fun getService(): AudioService = this@AudioService
    }
}