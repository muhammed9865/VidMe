package com.example.vidme.service.audio

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.media.session.MediaSessionCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import timber.log.Timber

class AudioCallback(private val context: Context, private val audioManager: AudioManager) :
    MediaSessionCompat.Callback() {

    override fun onSeekTo(pos: Long) {
        super.onSeekTo(pos)
        audioManager.seekTo(pos.toInt())
    }

    override fun onPause() {
        super.onPause()
        Timber.d("OnPause")
        val action = AudioActions.ACTION_PAUSE
        val intent = Intent(context, AudioReceiver::class.java)
        intent.action = AudioReceiver.ACTION
        intent.putExtra(AudioReceiver.ACTION_TYPE, action)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    override fun onPlay() {
        super.onPlay()
        Timber.d("OnPlay")
    }

    override fun onSkipToNext() {
        super.onSkipToNext()
        Timber.d("SkipTonEXT")
    }


    private fun generateAction(
        context: Context,
        action: String,
        requestCode: Int,
    ): PendingIntent {
        val intent = Intent(context, AudioReceiver::class.java)
        intent.action = AudioReceiver.ACTION
        intent.putExtra(AudioReceiver.ACTION_TYPE, action)
        return PendingIntent.getBroadcast(context,
            requestCode,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT + PendingIntent.FLAG_IMMUTABLE)
    }
}