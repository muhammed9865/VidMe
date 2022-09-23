package com.example.vidme.service.audio

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.media.session.MediaSessionCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.vidme.service.AudioReceiver
import com.example.vidme.service.AudioService
import timber.log.Timber

class AudioCallback(private val context: Context, private val audioManager: AudioManager) :
    MediaSessionCompat.Callback() {

    override fun onSeekTo(pos: Long) {
        super.onSeekTo(pos)
        Timber.d(pos.toString())
        val intent = Intent(context, AudioService::class.java)
        intent.action = AudioActions.ACTION_SEEK_TO
        intent.putExtra(AudioExtras.EXTRA_SEEK_TO_POSITION, pos.toInt())
        context.startService(intent)
    }

    override fun onPause() {
        super.onPause()
        val action = AudioActions.ACTION_PAUSE
        val intent = Intent(context, AudioReceiver::class.java)
        intent.action = AudioReceiver.ACTION
        intent.putExtra(AudioReceiver.ACTION_TYPE, action)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    override fun onPlay() {
        super.onPlay()
    }

    override fun onSkipToNext() {
        super.onSkipToNext()
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