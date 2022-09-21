package com.example.vidme.service.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.session.MediaButtonReceiver
import com.example.vidme.R
import com.example.vidme.di.NotificationModule
import com.example.vidme.domain.pojo.VideoInfo
import com.example.vidme.presentation.activity.main.MainActivity
import com.example.vidme.service.audio.AudioCallback
import com.example.vidme.service.audio.AudioManager
import com.example.vidme.service.audio.AudioReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

class NotificationManagerImpl @Inject constructor(
    private val notificationManager: NotificationManagerCompat,
    private var audioManager: AudioManager,
) :
    NotificationManager {

    @Inject
    @ApplicationContext
    lateinit var mContext: Context


    private var isPlaying = true
    private lateinit var currVideoInfo: VideoInfo
    private var requestCode = 0
    private var mediaSessionCompat: MediaSessionCompat? = null

    private fun getSession(context: Context): MediaSessionCompat {
        return if (mediaSessionCompat == null) {
            mediaSessionCompat = MediaSessionCompat(context, "Audio_player").apply {
                setCallback(AudioCallback(context, audioManager))
            }
            mediaSessionCompat!!
        } else mediaSessionCompat!!
    }

    private fun notificationBuilder(context: Context): NotificationCompat.Builder =
        NotificationCompat.Builder(context, NotificationModule.AUDIO_PLAYER_CHANNEL_ID).apply {
            val mediaSession = getSession(context)
            val style = androidx.media.app.NotificationCompat.MediaStyle().run {
                setMediaSession(mediaSession.sessionToken)

                setShowActionsInCompactView(0, 1, 2, 3)
            }
            setNotificationMetadata(mediaSession)
            setNotificationPlaybackState(mediaSession)

            style.setBuilder(this)

            setStyle(style)

            setContentTitle(context.getString(R.string.app_name))
            setSmallIcon(R.drawable.ic_audio)
            setContentText("Playing ${currVideoInfo.title}")
            priority = (NotificationCompat.PRIORITY_LOW)
            setOnlyAlertOnce(true)
            setShowWhen(false)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK + Intent.FLAG_ACTIVITY_NEW_TASK
            val pIntent =
                PendingIntent.getActivity(context, 20, intent, PendingIntent.FLAG_IMMUTABLE)

            setContentIntent(pIntent)

            color = context.getColor(R.color.toolbarColor)

            addAction(generateAction(context,
                R.drawable.ic_previous,
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS))


            val (action, icon) = if (isPlaying) {
                PlaybackStateCompat.ACTION_PAUSE to R.drawable.ic_pause
            } else {
                PlaybackStateCompat.ACTION_PLAY to R.drawable.ic_play
            }

            addAction(generateAction(context, icon, action))

            addAction(generateAction(context,
                R.drawable.ic_next,
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT))

            addAction(generateAction(context, R.drawable.ic_close, PlaybackStateCompat.ACTION_STOP))


        }


    private fun generateAction(
        context: Context,
        @DrawableRes icon: Int,
        action: Long,
        title: String? = null,
    ): NotificationCompat.Action {
        val pIntent = MediaButtonReceiver.buildMediaButtonPendingIntent(context, action)
        incrementCode()
        val intent = Intent(context, AudioReceiver::class.java)
        intent.action = AudioReceiver.ACTION
        intent.putExtra(AudioReceiver.ACTION_TYPE, action)
        return NotificationCompat.Action.Builder(icon, title, pIntent).build()
    }

    private fun incrementCode() {
        if (requestCode % 4 > 0 || requestCode == 0) {
            requestCode++
        } else requestCode = 0
    }

    private fun setNotificationMetadata(mediaSessionCompat: MediaSessionCompat) {
        val metadata = MediaMetadataCompat.Builder()
            .putLong(
                MediaMetadataCompat.METADATA_KEY_DURATION,
                audioManager.getVideoDuration()
            )
            .build()
        mediaSessionCompat.setMetadata(metadata)
    }

    private fun setNotificationPlaybackState(mediaSessionCompat: MediaSessionCompat) {
        val state = if (isPlaying) {
            PlaybackStateCompat.STATE_PLAYING
        } else PlaybackStateCompat.STATE_PAUSED
        val playbackState = PlaybackStateCompat.Builder()
            .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
            .setState(state,
                audioManager.getCurrentPosition().toLong(),
                1f)

        mediaSessionCompat.setPlaybackState(playbackState.build())
    }


    override fun buildNotification(context: Context): Notification {
        return notificationBuilder(context).build()
    }

    override fun setPlayingState(playing: Boolean) {
        isPlaying = playing
    }

    override fun show(context: Context) {
        val notification = buildNotification(context)
        notificationManager.notify(1337, notification)
    }

    override fun setCurrentVideoInfo(videoInfo: VideoInfo) {
        currVideoInfo = videoInfo
    }


    override fun onDestroy() {
        Timber.d("Notification Manager Destroyed")
    }
}
