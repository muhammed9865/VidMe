package com.example.vidme.service.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.session.MediaButtonReceiver
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.vidme.R
import com.example.vidme.di.NotificationModule
import com.example.vidme.domain.pojo.VideoInfo
import com.example.vidme.presentation.activity.main.MainActivity
import com.example.vidme.service.audio.AudioCallback
import com.example.vidme.service.audio.AudioManager
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

class NotificationManagerImpl @Inject constructor(
    private val notificationManager: NotificationManagerCompat,
    private var audioManager: AudioManager,
    @ApplicationContext private val mContext: Context,
) :
    NotificationManager {


    private var isPlaying = true
    private lateinit var currVideoInfo: VideoInfo
    private var requestCode = 0
    private var mediaSessionCompat: MediaSessionCompat? = null

    private val playbackStateBuilder = PlaybackStateCompat.Builder()
    private val metadataBuilder = MediaMetadataCompat.Builder()


    private fun getSession(context: Context): MediaSessionCompat {
        return if (mediaSessionCompat == null) {
            mediaSessionCompat = MediaSessionCompat(context, "Audio_player").apply {
                setCallback(AudioCallback(context))
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

            setContentIntent(getOnNotificationClickIntent(context))

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

    override fun getCurrentVideo(): VideoInfo {
        return currVideoInfo
    }

    private fun getOnNotificationClickIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.action = MainActivity.INTENT_FROM_NOTIFICATION_ACTION
        return PendingIntent.getActivity(context,
            20,
            intent,
            PendingIntent.FLAG_IMMUTABLE + PendingIntent.FLAG_CANCEL_CURRENT)

    }

    private fun generateAction(
        context: Context,
        @DrawableRes icon: Int,
        action: Long,
        title: String? = null,
    ): NotificationCompat.Action {

        val pIntent = MediaButtonReceiver.buildMediaButtonPendingIntent(context, action)
        incrementRequestCode()

        return NotificationCompat.Action.Builder(icon, title, pIntent).build()
    }

    private fun incrementRequestCode() {
        if (requestCode % 4 > 0 || requestCode == 0) {
            requestCode++
        } else requestCode = 0
    }

    private fun setNotificationMetadata(mediaSessionCompat: MediaSessionCompat) {
        metadataBuilder
            .putLong(
                MediaMetadataCompat.METADATA_KEY_DURATION,
                audioManager.getVideoDuration()
            )
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE,
                audioManager.getAudioData().single.title)
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, currVideoInfo.thumbnail)
        mediaSessionCompat.setMetadata(metadataBuilder.build())
    }

    private fun setNotificationPlaybackState(mediaSessionCompat: MediaSessionCompat) {
        val state = if (isPlaying) {
            PlaybackStateCompat.STATE_PLAYING
        } else {
            PlaybackStateCompat.STATE_PAUSED
        }

        val playbackState = playbackStateBuilder
            .setActions(PlaybackStateCompat.ACTION_SEEK_TO or PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or PlaybackStateCompat.ACTION_PAUSE
            )
            .setState(state,
                audioManager.getCurrentPosition().toLong(),
                1f)


        mediaSessionCompat.setPlaybackState(playbackState.build())
    }

    private fun getThumbnailBitmap(context: Context, url: String) {

        Glide.with(context)
            .asBitmap()
            .load(url)
            .centerCrop()
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON,
                        resource)
                    //show(context)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                    //show(context)
                }
            })


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
        getThumbnailBitmap(mContext, videoInfo.thumbnail)
    }


    override fun onDestroy() {
        mediaSessionCompat?.release()
        Timber.d("Notification Manager Destroyed")
    }
}
